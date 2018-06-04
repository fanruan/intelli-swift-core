package com.fr.swift.query.group.by;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.query.group.by.paging.GroupByPagingIterator;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.group.info.cursor.Cursor;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.row.GroupByResultSet;
import com.fr.swift.result.row.GroupByResultSetImpl;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.fr.swift.cube.io.IOConstant.NULL_DOUBLE;

/**
 * 这边group by作用于cube的分块数据（segment）
 * Created by Lyon on 2018/2/27.
 */
public class GroupByUtils {

    public static GroupByResultSet query(GroupByInfo groupByInfo, MetricInfo metricInfo, int pageSize) {
        boolean[] asc = getSorts(groupByInfo.getSorts(), groupByInfo.getDimensions().size());
        // TODO: 2018/4/20 expander过滤groupBy
        List<Column> dimensions = groupByInfo.getDimensions();
        Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> groupByIterator = new MultiDimensionGroupBy(
                dimensions, groupByInfo.getDetailFilter(), getCursor(null, dimensions), asc);
        if (pageSize != -1) {
            // 分页的情况
            groupByIterator = new GroupByPagingIterator(pageSize, groupByIterator);
        }
        List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> rowResult = new ArrayList<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>();
        List<Map<Integer, Object>> globalDictionaries= createGlobalDictionaries(dimensions.size());
        List<DictionaryEncodedColumn> dictionaries = getDictionaries(dimensions);
        while (groupByIterator.hasNext()) {
            KeyValue<RowIndexKey<int[]>, RowTraversal> keyValue = groupByIterator.next();
            int[] key = keyValue.getKey().getKey();
            AggregatorValue[] values = aggregateRow(keyValue.getValue(), metricInfo.getMetrics(), metricInfo.getAggregators());
            rowResult.add(new KeyValue<RowIndexKey<int[]>, AggregatorValue[]>(toGlobalIndex(key, dictionaries), values));
            updateGlobalDictionaries(key, globalDictionaries, dictionaries);
        }
        return new GroupByResultSetImpl(rowResult, globalDictionaries, dimensions.size());
    }

    static int[] getCursor(Cursor cursor, List<Column> dimensions) {
        if (cursor != null) {
            return cursor.createCursorIndex(getDictionaries(dimensions));
        }
        return new int[dimensions.size()];
    }

    static boolean[] getSorts(List<Sort> sorts, int dimensionSize) {
        boolean[] asc = new boolean[dimensionSize];
        // 默认为字典的升序
        Arrays.fill(asc, true);
        for (int i = 0; i < sorts.size(); i++) {
            if (sorts.get(i).getSortType() == SortType.DESC) {
                asc[sorts.get(i).getTargetIndex()] = false;
            }
        }
        return asc;
    }

    static List<DictionaryEncodedColumn> getDictionaries(List<Column> dimensions) {
        List<DictionaryEncodedColumn> dictionaries = new ArrayList<DictionaryEncodedColumn>();
        for (Column column : dimensions) {
            dictionaries.add(column.getDictionaryEncodedColumn());
        }
        return dictionaries;
    }

    static void updateGlobalDictionaries(int[] segmentIndexes, List<Map<Integer, Object>> globalDictionaries,
                                                 List<DictionaryEncodedColumn> dictionaries) {
        for (int i = 0; i < segmentIndexes.length; i++) {
            if (segmentIndexes[i] == -1) {
                continue;
            }
            int globalIndex = dictionaries.get(i).getGlobalIndexByIndex(segmentIndexes[i]);
            if (globalDictionaries.get(i).containsKey(globalIndex)) {
                continue;
            }
            globalDictionaries.get(i).put(globalIndex, dictionaries.get(i).getValue(segmentIndexes[i]));
        }
    }

    static List<Map<Integer, Object>> createGlobalDictionaries(int dimensionSize) {
        List<Map<Integer, Object>> dictionaries = new ArrayList<Map<Integer, Object>>();
        for (int i = 0; i < dimensionSize; i++) {
            dictionaries.add(new HashMap<Integer, Object>());
        }
        return dictionaries;
    }

    static RowIndexKey<int[]> toGlobalIndex(int[] segmentIndexes, List<DictionaryEncodedColumn> dictionaries) {
        int[] globalIndexes = new int[segmentIndexes.length];
        Arrays.fill(globalIndexes, -1);
        for (int i = 0; i < segmentIndexes.length; i++) {
            if (segmentIndexes[i] == -1) {
                continue;
            }
            globalIndexes[i] = dictionaries.get(i).getGlobalIndexByIndex(segmentIndexes[i]);
        }
        return new RowIndexKey<int[]>(globalIndexes);
    }

    static AggregatorValue[] aggregateRow(RowTraversal traversal, List<Column> metrics,
                                                  List<Aggregator> aggregators) {
        AggregatorValue[] values = new AggregatorValue[metrics.size()];
        for (int i = 0; i < metrics.size(); i++) {
            if (traversal.isEmpty()) {
                // 索引为空跳过
                values[i] = new DoubleAmountAggregatorValue(NULL_DOUBLE);
                continue;
            }
            // 如果指标比较多，这边也可以增加多线程计算
            values[i] = aggregators.get(i).aggregate(traversal, metrics.get(i));
        }
        return values;
    }
}

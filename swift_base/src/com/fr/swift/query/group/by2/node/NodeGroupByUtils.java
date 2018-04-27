package com.fr.swift.query.group.by2.node;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSetImpl;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.fr.swift.cube.io.IOConstant.NULL_DOUBLE;

/**
 * Created by Lyon on 2018/4/27.
 */
public class NodeGroupByUtils {

    public static NodeResultSet<GroupNode> groupBy(GroupByInfo groupByInfo, MetricInfo metricInfo) {
        GroupNode root = new GroupNode(-1, null);
        AggregatorValue[] values = new AggregatorValue[metricInfo.getTargetLength()];
        Arrays.fill(values, new DoubleAmountAggregatorValue(NULL_DOUBLE));
        root.setAggregatorValue(values);
        NodeRowMapper rowMapper = new NodeRowMapper(metricInfo);
        List<Map<Integer, Object>> rowGlobalDictionaries = initDictionaries(groupByInfo.getDimensions().size());
        List<DictionaryEncodedColumn> dictionaries = IteratorUtils.iterator2List(new MapperIterator<Column, DictionaryEncodedColumn>(groupByInfo.getDimensions().iterator(), new Function<Column, DictionaryEncodedColumn>() {
            @Override
            public DictionaryEncodedColumn apply(Column p) {
                return p.getDictionaryEncodedColumn();
            }
        }));
        Iterator<GroupNode[]> iterator = new MultiGroupByNodeIterator(groupByInfo, root, rowMapper);
        while (iterator.hasNext()) {
            GroupNode[] row = iterator.next();
            updateGlobalDictionariesAndGlobalIndex(row, rowGlobalDictionaries, dictionaries);
        }
        return new NodeMergeResultSetImpl<GroupNode>(root, rowGlobalDictionaries);
    }

    private static void updateGlobalDictionariesAndGlobalIndex(GroupNode[] row, List<Map<Integer, Object>> globalDictionaries,
                                                               List<DictionaryEncodedColumn> dictionaries) {
        for (int i = 0; i < row.length; i++) {
            if (row[i] == null) {
                break;
            }
            int globalIndex = dictionaries.get(i).getGlobalIndexByIndex(row[i].getDictionaryIndex());
            if (globalDictionaries.get(i).containsKey(globalIndex)) {
                continue;
            }
            globalDictionaries.get(i).put(globalIndex, dictionaries.get(i).getValue(row[i].getDictionaryIndex()));
            // 更新全局字典索引，减少一次循环
            row[i].setGlobalIndex(globalIndex);
        }
    }

    private static List<Map<Integer, Object>> initDictionaries(int dimensionSize) {
        List<Map<Integer, Object>> rowGlobalDictionaries = new ArrayList<Map<Integer, Object>>(dimensionSize);
        for (int i = 0; i < dimensionSize; i++) {
            rowGlobalDictionaries.add(new HashMap<Integer, Object>());
        }
        return rowGlobalDictionaries;
    }
}

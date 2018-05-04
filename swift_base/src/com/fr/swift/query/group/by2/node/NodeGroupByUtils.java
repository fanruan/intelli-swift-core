package com.fr.swift.query.group.by2.node;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by2.node.iterator.GroupNodeIterator;
import com.fr.swift.query.group.by2.node.mapper.GroupNodeRowMapper;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSetImpl;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.function.Function;
import com.fr.swift.util.function.Function2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/27.
 */
public class NodeGroupByUtils {

    public static NodeResultSet<GroupNode> groupBy(GroupByInfo groupByInfo, MetricInfo metricInfo) {
        GroupNode root = new GroupNode(-1, null);
        List<Map<Integer, Object>> rowGlobalDictionaries = initDictionaries(groupByInfo.getDimensions().size());
        List<DictionaryEncodedColumn> dictionaries = IteratorUtils.iterator2List(new MapperIterator<Column, DictionaryEncodedColumn>(groupByInfo.getDimensions().iterator(), new Function<Column, DictionaryEncodedColumn>() {
            @Override
            public DictionaryEncodedColumn apply(Column p) {
                return p.getDictionaryEncodedColumn();
            }
        }));
        if (groupByInfo.getDimensions().isEmpty()) {
            // 只有指标的情况
            aggregateRoot(root, groupByInfo.getDetailFilter().createFilterIndex(), metricInfo);
            return new NodeMergeResultSetImpl<GroupNode>(root, rowGlobalDictionaries, metricInfo.getAggregators());
        }
        GroupNodeRowMapper rowMapper = new GroupNodeRowMapper(metricInfo);
        Function2<Integer, GroupByEntry, GroupNode> itemMapper = new Function2<Integer, GroupByEntry, GroupNode>() {
            @Override
            public GroupNode apply(Integer deep, GroupByEntry groupByEntry) {
                // 这边先存segment的字典序号吧
                return new GroupNode((int) deep, groupByEntry.getIndex());
            }
        };
        Iterator<GroupNode[]> iterator = new GroupNodeIterator<GroupNode>(groupByInfo, root, itemMapper, rowMapper);
        while (iterator.hasNext()) {
            GroupNode[] row = iterator.next();
            updateGlobalDictionariesAndGlobalIndex(row, rowGlobalDictionaries, dictionaries);
        }
        return new NodeMergeResultSetImpl<GroupNode>(root, rowGlobalDictionaries, metricInfo.getAggregators());
    }

    private static void aggregateRoot(GroupNode root, RowTraversal traversal, MetricInfo metricInfo) {
        AggregatorValue[] values = GroupNodeRowMapper.aggregateRow(traversal, metricInfo.getTargetLength(),
                metricInfo.getMetrics(), metricInfo.getAggregators());
        root.setAggregatorValue(values);
    }

    static void updateGlobalDictionariesAndGlobalIndex(GroupNode[] row, List<Map<Integer, Object>> globalDictionaries,
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

    static List<Map<Integer, Object>> initDictionaries(int dimensionSize) {
        List<Map<Integer, Object>> rowGlobalDictionaries = new ArrayList<Map<Integer, Object>>(dimensionSize);
        for (int i = 0; i < dimensionSize; i++) {
            rowGlobalDictionaries.add(new HashMap<Integer, Object>());
        }
        return rowGlobalDictionaries;
    }
}

package com.fr.swift.query.group.by2.node;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by2.node.iterator.GroupNodeIterator;
import com.fr.swift.query.group.by2.node.mapper.GroupNodeRowMapper;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.group.info.cursor.Cursor;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeMergeResultSetImpl;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.function.BinaryFunction;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/27.
 */
public class NodeGroupByUtils {

    /**
     * 聚合单块segment数据，得到node结果集和压缩的字典值
     *
     * @param groupByInfo
     * @param metricInfo
     * @param pageSize  Integer.MAX_VALUE为不分页
     * @return
     */
    public static Iterator<NodeMergeResultSet<GroupNode>> groupBy(GroupByInfo groupByInfo, MetricInfo metricInfo, int pageSize) {
        if (groupByInfo.getDimensions().isEmpty()) {
            // 只有指标的情况
            GroupNode root = new GroupNode(-1, null);
            aggregateRoot(root, groupByInfo.getDetailFilter().createFilterIndex(), metricInfo);
            List<NodeMergeResultSet<GroupNode>> list = new ArrayList<NodeMergeResultSet<GroupNode>>();
            list.add(new NodeMergeResultSetImpl<GroupNode>(root, new ArrayList<Map<Integer, Object>>()));
            return list.iterator();
        }
        return new ResultSetIterator(pageSize, groupByInfo, metricInfo);
    }

    private static class ResultSetIterator implements Iterator<NodeMergeResultSet<GroupNode>> {

        private int pageSize;
        private GroupByInfo groupByInfo;
        private MetricInfo metricInfo;
        private boolean hasNextPage;
        private NodeMergeResultSet<GroupNode> next;

        public ResultSetIterator(int pageSize, GroupByInfo groupByInfo, MetricInfo metricInfo) {
            this.pageSize = pageSize;
            this.groupByInfo = groupByInfo;
            this.metricInfo = metricInfo;
            this.next = getNext();
        }

        // TODO: 2018/6/14 这边看起来是维持迭代器状态的实现，实际却是每次都是重新初始化groupBy迭代器，后面可以尝试优化
        private NodeMergeResultSet<GroupNode> getNext() {
            GroupNode root = new GroupNode(-1, null);
            List<Map<Integer, Object>> rowGlobalDictionaries = initDictionaries(groupByInfo.getDimensions().size());
            List<DictionaryEncodedColumn> dictionaries = IteratorUtils.iterator2List(new MapperIterator<Column, DictionaryEncodedColumn>(groupByInfo.getDimensions().iterator(), new Function<Column, DictionaryEncodedColumn>() {
                @Override
                public DictionaryEncodedColumn apply(Column p) {
                    return p.getDictionaryEncodedColumn();
                }
            }));
            GroupNodeRowMapper rowMapper = new GroupNodeRowMapper(metricInfo);
            BinaryFunction<Integer, GroupByEntry, GroupNode> itemMapper = new BinaryFunction<Integer, GroupByEntry, GroupNode>() {
                @Override
                public GroupNode apply(Integer deep, GroupByEntry groupByEntry) {
                    // 这边先存segment的字典序号吧
                    return new GroupNode((int) deep, groupByEntry.getIndex());
                }
            };
            Iterator<GroupNode[]> iterator = new GroupNodeIterator<GroupNode>(groupByInfo, root, itemMapper, rowMapper);
            int count = pageSize;
            while (iterator.hasNext() && count-- > 0) {
                GroupNode[] row = iterator.next();
                updateGlobalDictionariesAndGlobalIndex(row, rowGlobalDictionaries, dictionaries);
            }
            hasNextPage = iterator.hasNext();
            if (hasNextPage) {
                final int[] cursor = new int[groupByInfo.getDimensions().size()];
                GroupNode tmp = root;
                while (tmp.getChildrenSize() != 0) {
                    cursor[tmp.getDepth()] = tmp.getChild(tmp.getChildrenSize() - 1).getIndex();
                }
                // 这边更新一下cursor
                groupByInfo = new GroupByInfoImpl(groupByInfo.getDimensions(), groupByInfo.getDetailFilter(), groupByInfo.getSorts(), groupByInfo.getExpander(), new Cursor() {
                    @Override
                    public int[] createCursorIndex(List<DictionaryEncodedColumn> columns) {
                        return cursor;
                    }
                });
            }
            return new NodeMergeResultSetImpl<GroupNode>(root, rowGlobalDictionaries);
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public NodeMergeResultSet<GroupNode> next() {
            NodeMergeResultSet<GroupNode> ret = next;
            next = null;
            if (hasNextPage) {
                next = getNext();
            }
            return ret;
        }

        @Override
        public void remove() {
        }
    }

    private static void aggregateRoot(GroupNode root, RowTraversal traversal, MetricInfo metricInfo) {
        AggregatorValue[] values = GroupNodeRowMapper.aggregateRow(traversal, metricInfo.getTargetLength(),
                metricInfo.getMetrics(), metricInfo.getAggregators());
        root.setAggregatorValue(values);
    }

    static void updateGlobalDictionariesAndGlobalIndex(GroupNode[] row, List<Map<Integer, Object>> globalDictionaries,
                                                       List<DictionaryEncodedColumn> dictionaries) {
        for (int i = 0; i < row.length; i++) {
            if (row[i].isGlobalIndexUpdated()) {
                continue;
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

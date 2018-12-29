//package com.fr.swift.query.post.utils;
//
//import com.fr.swift.compare.Comparators;
//import com.fr.swift.db.SwiftDatabase;
//import com.fr.swift.query.aggregator.AggregatorValue;
//import com.fr.swift.query.aggregator.Combiner;
//import com.fr.swift.query.info.element.dimension.Dimension;
//import com.fr.swift.result.GroupNode;
//import com.fr.swift.result.NodeResultSet;
//import com.fr.swift.result.NodeResultSetImpl;
//import com.fr.swift.source.SwiftMetaData;
//import com.fr.swift.source.SwiftMetaDataColumn;
//import com.fr.swift.structure.Pair;
//import com.fr.swift.structure.iterator.MapperIterator;
//import com.fr.swift.structure.iterator.Tree2RowIterator;
//import com.fr.swift.structure.queue.SortedListMergingUtils;
//import com.fr.swift.util.function.Function;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * Created by Lyon on 2018/5/31.
// */
//public class ResultJoinUtils {
//
//    /**
//     * 对过个查询结果根据维度合并
//     * todo 这边处理地略繁琐，后面看一下怎么改进
//     *
//     * @param resultSets 多个查询结果
//     * @param dimensions 合并依据的维度
//     * @return
//     * @throws SQLException
//     */
//    public static NodeResultSet join(final List<NodeResultSet<GroupNode>> resultSets, List<Dimension> dimensions) throws SQLException {
//        int dimensionSize = dimensions.size();
//        final List<Integer> metricLengthList = getMetricLengthList(dimensionSize, resultSets);
//        List<Iterator<MergeRow>> iterators = new ArrayList<Iterator<MergeRow>>();
//        for (int i = 0; i < resultSets.size(); i++) {
//            final GroupNode root = resultSets.get(i).getPage().getKey();
//            Iterator<List<GroupNode>> iterator = new Tree2RowIterator<GroupNode>(dimensionSize, root.getChildren().iterator(), new Function<GroupNode, Iterator<GroupNode>>() {
//                @Override
//                public Iterator<GroupNode> apply(GroupNode p) {
//                    return p.getChildren().iterator();
//                }
//            });
//            final int resultSetIndex = i;
//            Iterator<MergeRow> rowIterator = new MapperIterator<List<GroupNode>, MergeRow>(iterator, new Function<List<GroupNode>, MergeRow>() {
//                @Override
//                public MergeRow apply(List<GroupNode> p) {
//                    MergeRow row = new MergeRow(resultSetIndex, metricLengthList, p);
//                    row.setValue(resultSetIndex, p.get(p.size() - 1).getAggregatorValue());
//                    return row;
//                }
//            });
//            iterators.add(rowIterator);
//        }
//        Iterator<MergeRow> iterator = SortedListMergingUtils.mergeIterator(iterators, new MergeRowComparator(), new MergeRowCombiner());
//        Iterator<Pair<List<Object>, AggregatorValue[]>> rowIt = new MapperIterator<MergeRow, Pair<List<Object>, AggregatorValue[]>>(iterator, new Function<MergeRow, Pair<List<Object>, AggregatorValue[]>>() {
//            @Override
//            public Pair<List<Object>, AggregatorValue[]> apply(MergeRow p) {
//                return Pair.of(p.getKey(), p.getAllValues());
//            }
//        });
//        return new NodeResultSetImpl(resultSets.get(0).getFetchSize(), createNode(dimensionSize, rowIt), crateMetaData(resultSets, dimensions));
//    }
//
//    public static GroupNode createNode(int dimensionSize, Iterator<Pair<List<Object>, AggregatorValue[]>> iterator) {
//        // GroupNode各层的节点缓存，第一层为根节点
//        Object[] cachedNode = new Object[dimensionSize + 1];
//        Arrays.fill(cachedNode, null);
//        // 缓存上一次插入的一行数据对于的各个维度的索引
//        Object[] cachedKey = new Object[dimensionSize];
//        GroupNode root = new GroupNode(-1, null);
//        cachedNode[0] = root;
//        while (iterator.hasNext()) {
//            Pair<List<Object>, AggregatorValue[]> row = iterator.next();
//            List<Object> key = row.getKey();
//            AggregatorValue[] values = row.getValue();
//            int deep = 0;
//            for (; deep < key.size(); deep++) {
//                if (key.get(deep) == null) {
//                    break;
//                }
//                if (cachedNode[deep + 1] == null || cachedKey[deep] == null || !cachedKey[deep].equals(key.get(deep))) {
//                    // 刷新缓存索引，deep之后的索引都无效了
//                    Arrays.fill(cachedKey, deep, cachedKey.length, null);
//                    // cachedNode和cachedIndex是同步更新的
//                    cachedNode[deep + 1] = new GroupNode(deep, key.get(deep));
//                    cachedKey[deep] = key.get(deep);
//                    GroupNode node = (GroupNode) cachedNode[deep];
//                    node.addChild((GroupNode) cachedNode[deep + 1]);
//                }
//            }
//            // 给当前kv所代表的行设置值
//            ((GroupNode) cachedNode[deep]).setAggregatorValue(values);
//        }
//        return root;
//    }
//
//    private static SwiftMetaData crateMetaData(List<NodeResultSet<GroupNode>> resultSets, List<Dimension> dimensions) throws SQLException {
//        final List<String> columnNames = getColumnNames(dimensions, resultSets);
//        return new SwiftMetaData() {
//            @Override
//            public SwiftDatabase getSwiftDatabase() {
//                return null;
//            }
//
//            @Override
//            public String getSchemaName() {
//                return null;
//            }
//
//            @Override
//            public String getTableName() {
//                return null;
//            }
//
//            @Override
//            public int getColumnCount() {
//                return columnNames.size();
//            }
//
//            @Override
//            public String getColumnName(int index) {
//                return columnNames.get(index);
//            }
//
//            @Override
//            public String getColumnRemark(int index) {
//                return null;
//            }
//
//            @Override
//            public int getColumnType(int index) {
//                return 0;
//            }
//
//            @Override
//            public int getPrecision(int index) {
//                return 0;
//            }
//
//            @Override
//            public int getScale(int index) {
//                return 0;
//            }
//
//            @Override
//            public SwiftMetaDataColumn getColumn(int index) {
//                return null;
//            }
//
//            @Override
//            public SwiftMetaDataColumn getColumn(String columnName) {
//                return null;
//            }
//
//            @Override
//            public int getColumnIndex(String columnName) {
//                return columnNames.indexOf(columnName);
//            }
//
//            @Override
//            public String getColumnId(int index) {
//                return null;
//            }
//
//            @Override
//            public String getColumnId(String columnName) {
//                return null;
//            }
//
//            @Override
//            public String getRemark() {
//                return null;
//            }
//
//            @Override
//            public List<String> getFieldNames() {
//                return columnNames;
//            }
//
//            @Override
//            public String getId() {
//                return null;
//            }
//        };
//    }
//
//    private static List<Integer> getMetricLengthList(int dimensionSize, List<NodeResultSet<GroupNode>> resultSets) throws SQLException {
//        List<Integer> list = new ArrayList<Integer>();
//        for (NodeResultSet<GroupNode> resultSet : resultSets) {
//            int len = resultSet.getMetaData().getFieldNames().size() - dimensionSize;
//            list.add(len);
//        }
//        return list;
//    }
//
//    private static List<String> getColumnNames(List<Dimension> dimensions, List<NodeResultSet<GroupNode>> resultSets) throws SQLException {
//        List<String> names = new ArrayList<String>();
//        for (Dimension dimension : dimensions) {
//            names.add(dimension.getColumnKey().getName());
//        }
//        int dimensionSize = dimensions.size();
//        for (NodeResultSet resultSet : resultSets) {
//            List<String> fieldNames = resultSet.getMetaData().getFieldNames();
//            names.addAll(fieldNames.subList(dimensionSize, fieldNames.size()));
//        }
//        return names;
//    }
//
//    private static class MergeRowCombiner implements Combiner<MergeRow> {
//        @Override
//        public void combine(MergeRow current, MergeRow other) {
//            current.setValue(other.getResultSetIndex(), other.getSelfValue());
//        }
//    }
//
//    private static class MergeRowComparator implements Comparator<MergeRow> {
//
//        @Override
//        public int compare(MergeRow o1, MergeRow o2) {
//            List<?> key1 = o1.getKey();
//            List<?> key2 = o2.getKey();
//            assert key1.size() == key2.size();
//            for (int i = 0; i < key1.size(); i++) {
//                String s1 = key1.get(i) == null ? null : key1.get(i).toString();
//                String s2 = key2.get(i) == null ? null : key2.get(i).toString();
//                if (Comparators.STRING_ASC.compare(s1, s2) > 0) {
//                    return 1;
//                }
//                if (Comparators.STRING_ASC.compare(s1, s2) < 0) {
//                    return -1;
//                }
//            }
//            return 0;
//        }
//    }
//
//    private static class MergeRow {
//
//        private int resultSetIndex;
//        private List<Object> key;
//        private AggregatorValue[][] values;
//
//        public MergeRow(int resultSetIndex, List<Integer> metricLengthList, List<GroupNode> nodes) {
//            this.resultSetIndex = resultSetIndex;
//            initKey(nodes);
//            initValues(metricLengthList);
//        }
//
//        private void initKey(List<GroupNode> nodes) {
//            key = new ArrayList<Object>();
//            for (GroupNode node : nodes) {
//                key.add(node.getData());
//            }
//        }
//
//        private void initValues(List<Integer> metricLengthList) {
//            this.values = new AggregatorValue[metricLengthList.size()][];
//            for (int i = 0; i < values.length; i++) {
//                values[i] = new AggregatorValue[metricLengthList.get(i)];
//            }
//        }
//
//        public void setValue(int index, AggregatorValue[] value) {
//            values[index] = value;
//        }
//
//        public AggregatorValue[] getSelfValue() {
//            return values[resultSetIndex];
//        }
//
//        public AggregatorValue[] getAllValues() {
//            List<AggregatorValue> total = new ArrayList<AggregatorValue>();
//            for (AggregatorValue[] value : values) {
//                for (int i = 0; i < value.length; i++) {
//                    total.add(value[i]);
//                }
//            }
//            return total.toArray(new AggregatorValue[total.size()]);
//        }
//
//        public List<Object> getKey() {
//            return key;
//        }
//
//        public int getResultSetIndex() {
//            return resultSetIndex;
//        }
//    }
//
//}

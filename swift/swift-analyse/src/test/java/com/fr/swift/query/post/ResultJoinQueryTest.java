//package com.fr.swift.query.post;
//
//import com.fr.stable.StringUtils;
//import com.fr.swift.query.aggregator.AggregatorValue;
//import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
//import com.fr.swift.query.info.element.dimension.Dimension;
//import com.fr.swift.query.query.Query;
//import com.fr.swift.result.GroupNode;
//import com.fr.swift.result.NodeResultSet;
//import com.fr.swift.result.NodeResultSetImpl;
//import com.fr.swift.segment.column.ColumnKey;
//import com.fr.swift.source.Row;
//import com.fr.swift.source.SwiftMetaData;
//import com.fr.swift.structure.Pair;
//import junit.framework.TestCase;
//import org.easymock.EasyMock;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Lyon on 2018/6/1.
// */
//public class ResultJoinQueryTest extends TestCase {
//
//    private List<Query<NodeResultSet>> queries;
//    private List<Dimension> dimensions;
//
//    @Override
//    protected void setUp() throws Exception {
//        List<GroupNode> roots = new ArrayList<>();
//        roots.add(PostQueryTestUtils.createNode(1, new Pair[]{
//                Pair.of(new Object[]{"a"}, new int[]{1}),
//                Pair.of(new Object[]{"b"}, new int[]{1}),
//        }));
//        roots.add(PostQueryTestUtils.createNode(1, new Pair[]{
//                Pair.of(new Object[]{"b"}, new int[]{1}),
//                Pair.of(new Object[]{"c"}, new int[]{1}),
//        }));
//        roots.add(PostQueryTestUtils.createNode(1, new Pair[]{
//                Pair.of(new Object[]{"a"}, new int[]{1}),
//                Pair.of(new Object[]{"c"}, new int[]{1}),
//        }));
//        String[] tableNames = new String[]{"a", "b", "c"};
//        String[][] columnNames = new String[][]{
//                new String[]{"dim", "a_metric1"},
//                new String[]{"dim", "b_metric1"},
//                new String[]{"dim", "c_metric1"},
//        };
//        List<SwiftMetaData> metaDataList = PostQueryTestUtils.createMetaData(tableNames, columnNames);
//        queries = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            final int index = i;
//            Query<NodeResultSet> query = new PostQuery<NodeResultSet>() {
//                @Override
//                public NodeResultSet getQueryResult() {
//                    return new NodeResultSetImpl(200, roots.get(index), metaDataList.get(index));
//                }
//            };
//            queries.add(query);
//        }
//        dimensions = new ArrayList<>();
//        Dimension dimension = EasyMock.mock(Dimension.class);
//        ColumnKey columnKey = EasyMock.mock(ColumnKey.class);
//        EasyMock.expect(columnKey.getName()).andReturn("dim").times(1);
//        EasyMock.expect(dimension.getColumnKey()).andReturn(columnKey).times(1);
//        EasyMock.replay(dimension);
//        EasyMock.replay(columnKey);
//        dimensions.add(dimension);
//    }
//
//    public void test() {
//        PostQuery<NodeResultSet> query = new ResultJoinQuery(queries, dimensions);
//        try {
//            NodeResultSet resultSet = query.getQueryResult();
//            assertTrue(resultSet.hasNext());
//            Row row = resultSet.getNextRow(); // ["a", 1, null, 1]
//            assertEquals(row.getSize(), 1 + 3);
//            assertTrue(StringUtils.equals(row.getValue(0).toString(), "a"));
//            assertEquals(row.getValue(1), 1.);
//            assertEquals(row.getValue(2), null);
//            assertEquals(row.getValue(3), 1.);
//
//            assertTrue(resultSet.hasNext());
//            row = resultSet.getNextRow(); // ["b", 1, 1, null]
//            assertEquals(row.getSize(), 1 + 3);
//            assertTrue(StringUtils.equals(row.getValue(0).toString(), "b"));
//            assertEquals(row.getValue(1), 1.);
//            assertEquals(row.getValue(2), 1.);
//            assertEquals(row.getValue(3), null);
//
//            assertTrue(resultSet.hasNext());
//            row = resultSet.getNextRow(); // ["c", null, 1, 1]
//            assertEquals(row.getSize(), 1 + 3);
//            assertTrue(StringUtils.equals(row.getValue(0).toString(), "c"));
//            assertEquals(row.getValue(1), null);
//            assertEquals(row.getValue(2), 1.);
//            assertEquals(row.getValue(3), 1.);
//
//        } catch (SQLException e) {
//            assertTrue(false);
//        }
//    }
//
//    private static GroupNode createNode(String[] keys, int[] values) {
//        AggregatorValue[] aggregatorValues = new AggregatorValue[values.length];
//        for (int i = 0; i < aggregatorValues.length; i++) {
//            aggregatorValues[i] = new DoubleAmountAggregatorValue(values[i]);
//        }
//        GroupNode root = new GroupNode(-1, null);
//        for (int i = 0; i < keys.length; i++) {
//            GroupNode node = new GroupNode(1, keys[i]);
//            node.setAggregatorValue(aggregatorValues);
//            root.addChild(node);
//        }
//        return root;
//    }
//}

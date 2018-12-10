package com.fr.swift.query.segment.group;

import com.fr.swift.source.Row;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by Lyon on 2018/7/26.
 */
public class GroupPagingSegmentQueryTest extends TestCase {

    private List<Row> rowList;
//    private CubeData cubeData;
//
//    @Before
//    public void setUp() throws Exception {
//
//        cubeData = new CubeData();
//        GroupByInfo groupByInfo = new GroupByInfoImpl(20, cubeData.getDimensions(), new DetailFilter() {
//            @Override
//            public ImmutableBitMap createFilterIndex() {
//                return BitMaps.newAllShowBitMap(cubeData.getRowCount());
//            }
//
//            @Override
//            public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
//                return false;
//            }
//        }, new ArrayList<>(), null);
//        MetricInfo metricInfo = new MetricInfoImpl(cubeData.getMetrics(), cubeData.getAggregators(), cubeData.getMetrics().size());
//        NodeMergeResultSet<GroupNode> resultSet = (NodeMergeResultSet<GroupNode>) new GroupPagingSegmentQuery(groupByInfo, metricInfo).getQueryResult();
//        rowList = new ArrayList<>();
//        while (resultSet.hasNextPage()) {
//            Pair<GroupNode, List<Map<Integer, Object>>> pair = resultSet.getPage();
//            GroupNode node = pair.getKey();
//            GroupNodeUtils.updateNodeData(node, pair.getValue());
//            rowList.addAll(IteratorUtils.iterator2List(SwiftNodeUtils.node2RowIterator(node)));
//        }
//    }
//
//    @Test
//    public void test() {
//        Map<RowIndexKey<int[]>, double[]> result = cubeData.getAggregationResult();
//        assertEquals(rowList.size(), result.size());
//        try {
//            for (Row row : rowList) {
//                RowIndexKey<int[]> key = getKey(row);
//                Assert.assertTrue(result.containsKey(key));
//                double[] values = getValues(row);
//                Assert.assertTrue(Arrays.equals(values, result.get(key)));
//            }
//        } catch (Exception e) {
//            assertTrue(false);
//        }
//    }
//
//    private double[] getValues(Row row) {
//        double[] result = new double[cubeData.getMetrics().size()];
//        int offset = cubeData.getDimensions().size();
//        for (int i = 0; i < cubeData.getMetrics().size(); i++) {
//            result[i] = row.getValue(i + offset);
//        }
//        return result;
//    }
//
//    private RowIndexKey<int[]> getKey(Row row) {
//        int dimensionSize = cubeData.getDimensions().size();
//        int[] key = new int[dimensionSize];
//        for (int i = 0; i < dimensionSize; i++) {
//            key[i] = cubeData.getDimensions().get(i).getKey().getDictionaryEncodedColumn().getIndex(row.getValue(i));
//        }
//        return new RowIndexKey<>(key);
//    }
}

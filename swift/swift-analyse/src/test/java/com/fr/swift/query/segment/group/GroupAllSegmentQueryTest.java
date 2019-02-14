package com.fr.swift.query.segment.group;

import org.junit.Ignore;

/**
 * Created by Lyon on 2018/5/31.
 */
@Ignore
public class GroupAllSegmentQueryTest {

//    private NodeMergeResultSet<GroupNode> resultSet;
//    private CubeData cubeData;
//
//    @Before
//    public void setUp() throws Exception {
//
//        cubeData = new CubeData();
//        GroupByInfo groupByInfo = new GroupByInfoImpl(Integer.MAX_VALUE, cubeData.getDimensions(), new DetailFilter() {
//            @Override
//            public ImmutableBitMap createFilterIndex() {
//                return BitMaps.newAllShowBitMap(cubeData.getRowCount());
//            }
//
//            @Override
//            public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
//                return false;/**/
//            }
//        }, new ArrayList<>(), null);
//        MetricInfo metricInfo = new MetricInfoImpl(cubeData.getMetrics(), cubeData.getAggregators(), cubeData.getMetrics().size());
//        resultSet = (NodeMergeResultSet<GroupNode>) new GroupAllSegmentQuery(groupByInfo, metricInfo).getQueryResult();
//        // 更新Node#data
//        Pair<GroupNode, List<Map<Integer, Object>>> pair = resultSet.getPage();
//        GroupNodeUtils.updateNodeData(pair.getKey(), pair.getValue());
//    }
//
//    @Test
//    public void test() {
//        Map<RowIndexKey<int[]>, double[]> result = cubeData.getAggregationResult();
//        try {
//            while (resultSet.hasNext()) {
//                Row row = resultSet.getNextRow();
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

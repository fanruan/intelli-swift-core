package com.fr.swift.result.node.resultset;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.query.group.by.CubeData;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.group.info.MetricInfoImpl;
import com.fr.swift.query.segment.group.GroupPagingSegmentQuery;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.result.node.GroupNodeUtils;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.IteratorUtils;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Lyon on 2018/7/27.
 */
public class ChainedNodeMergeResultSetTest extends TestCase {

    private static NodeMergeResultSet<GroupNode> prepareResultSet(int pageSize, CubeData cubeData) {
        GroupByInfo groupByInfo = new GroupByInfoImpl(pageSize, cubeData.getDimensions(), new DetailFilter() {
            @Override
            public ImmutableBitMap createFilterIndex() {
                return BitMaps.newAllShowBitMap(cubeData.getRowCount());
            }

            @Override
            public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
                return false;
            }
        }, new ArrayList<>(), null);
        MetricInfo metricInfo = new MetricInfoImpl(cubeData.getMetrics(), cubeData.getAggregators(), cubeData.getMetrics().size());
        return (NodeMergeResultSet<GroupNode>) new GroupPagingSegmentQuery(groupByInfo, metricInfo).getQueryResult();
    }

    private static List<Comparator<GroupNode>> prepareComparators(int dimensionSize) {
        List<Comparator<GroupNode>> comparators = new ArrayList<>();
        for (int i = 0; i < dimensionSize; i++) {
            comparators.add(new Comparator<GroupNode>() {
                @Override
                public int compare(GroupNode o1, GroupNode o2) {
                    return Integer.compare(o1.getDictionaryIndex(), o2.getDictionaryIndex());
                }
            });
        }
        return comparators;
    }

    public void testMultiSegmentsPaging() {
        CubeData cubeData1 = new CubeData();
        CubeData cubeData2 = new CubeData();
        CubeData cubeData3 = new CubeData();
        CubeData.prepareGlobalIndex(cubeData1, cubeData2, cubeData3);
        int fetchSize = 17;
        NodeMergeResultSet<GroupNode> resultSet1 = prepareResultSet(fetchSize, cubeData1);
        NodeMergeResultSet<GroupNode> resultSet2 = prepareResultSet(fetchSize, cubeData2);
        NodeMergeResultSet<GroupNode> resultSet3 = prepareResultSet(fetchSize, cubeData3);
        boolean[] isGlobalIndexed = new boolean[cubeData1.getDimensions().size()];
        Arrays.fill(isGlobalIndexed, true);
        ChainedNodeMergeResultSet mergeResultSet = new ChainedNodeMergeResultSet(fetchSize, isGlobalIndexed,
                Arrays.asList(resultSet1, resultSet2, resultSet3), cubeData1.getAggregators(),
                prepareComparators(cubeData1.getDimensions().size()));
        List<Row> expected = mergeCubeData(cubeData1, cubeData2, cubeData3);
        List<Row> actual = getRowList(mergeResultSet);
        testMultiSegments(expected, actual);
    }

    public void testMultiSegmentsAll() {
        CubeData cubeData1 = new CubeData();
        CubeData cubeData2 = new CubeData();
        CubeData cubeData3 = new CubeData();
        CubeData.prepareGlobalIndex(cubeData1, cubeData2, cubeData3);
        int fetchSize = Integer.MAX_VALUE;
        NodeMergeResultSet<GroupNode> resultSet1 = prepareResultSet(fetchSize, cubeData1);
        NodeMergeResultSet<GroupNode> resultSet2 = prepareResultSet(fetchSize, cubeData2);
        NodeMergeResultSet<GroupNode> resultSet3 = prepareResultSet(fetchSize, cubeData3);
        boolean[] isGlobalIndexed = new boolean[cubeData1.getDimensions().size()];
        Arrays.fill(isGlobalIndexed, true);
        ChainedNodeMergeResultSet mergeResultSet = new ChainedNodeMergeResultSet(fetchSize, isGlobalIndexed,
                Arrays.asList(resultSet1, resultSet2, resultSet3), cubeData1.getAggregators(),
                prepareComparators(cubeData1.getDimensions().size()));
        List<Row> expected = mergeCubeData(cubeData1, cubeData2, cubeData3);
        List<Row> actual = getRowList(mergeResultSet);
        testMultiSegments(expected, actual);
    }

    private void testMultiSegments(List<Row> expected, List<Row> actual) {
        assertTrue(actual.size() == expected.size());
        for (Row row : actual) {
            int index = expected.indexOf(row);
            assertTrue(index != -1);
            Row e = expected.get(index);
            assertEquals(e, row);
        }
    }

    private List<Row> mergeCubeData(CubeData... cubes) {
        List<Map<RowIndexKey<String[]>, double[]>> mapList = new ArrayList<>();
        for (CubeData cubeData : cubes) {
            Map<RowIndexKey<int[]>, double[]> result = cubeData.getAggregationResult();
            Map<RowIndexKey<String[]>, double[]> strMap = new HashMap<>();
            for (Map.Entry<RowIndexKey<int[]>, double[]> entry : result.entrySet()) {
                int[] indexes = entry.getKey().getKey();
                String[] strings = new String[indexes.length];
                for (int i = 0; i < indexes.length; i++) {
                    strings[i] = (String) cubeData.getDimensions().get(i).getKey().getDictionaryEncodedColumn().getValue(indexes[i]);
                }
                strMap.put(new RowIndexKey<>(strings), entry.getValue());
            }
            mapList.add(strMap);
        }
        Map<RowIndexKey<String[]>, double[]> result = mapList.get(0);
        for (int i = 1; i < mapList.size(); i++) {
            Map<RowIndexKey<String[]>, double[]> map = mapList.get(i);
            for (Map.Entry<RowIndexKey<String[]>, double[]> entry : map.entrySet()) {
                if (!result.containsKey(entry.getKey())) {
                    result.put(entry.getKey(), entry.getValue());
                    continue;
                }
                double[] r = result.get(entry.getKey());
                double[] e = entry.getValue();
                for (int j = 0; j < r.length; j++) {
                    r[j] += e[j];
                }
            }
        }
        List<Row> rows = new ArrayList<>();
        for (Map.Entry<RowIndexKey<String[]>, double[]> entry : result.entrySet()) {
            List row = new ArrayList();
            row.addAll(Arrays.asList(entry.getKey().getKey()));
            double[] values = entry.getValue();
            for (int i = 0; i < values.length; i++) {
                row.add(values[i]);
            }
            rows.add(new ListBasedRow(row));
        }
        return rows;
    }

    public void testOneSegment() {
        CubeData cubeData = new CubeData();
        // 测试分页
        test(20, cubeData);
        // 测试不分页
        test(Integer.MAX_VALUE, cubeData);
    }

    private void test(int fetchSize, CubeData cubeData) {
        boolean[] isGlobalIndexed = new boolean[cubeData.getDimensions().size()];
        Arrays.fill(isGlobalIndexed, true);
        ChainedNodeMergeResultSet mergeResultSet = new ChainedNodeMergeResultSet(fetchSize, isGlobalIndexed,
                Collections.singletonList(prepareResultSet(fetchSize, cubeData)), cubeData.getAggregators(),
                prepareComparators(cubeData.getDimensions().size()));
        List<Row> rowList = getRowList(mergeResultSet);
        Map<RowIndexKey<int[]>, double[]> result = cubeData.getAggregationResult();
        assertEquals(rowList.size(), result.size());
        try {
            for (Row row : rowList) {
                RowIndexKey<int[]> key = getKey(row, cubeData);
                Assert.assertTrue(result.containsKey(key));
                double[] values = getValues(row, cubeData);
                Assert.assertTrue(Arrays.equals(values, result.get(key)));
            }
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    private static List<Row> getRowList(NodeMergeResultSet mergeResultSet) {
        List<Row> rowList = new ArrayList<>();
        while (mergeResultSet.hasNextPage()) {
            Pair<GroupNode, List<Map<Integer, Object>>> pair = mergeResultSet.getPage();
            GroupNode node = pair.getKey();
            GroupNodeUtils.updateNodeData(node, pair.getValue());
            rowList.addAll(IteratorUtils.iterator2List(SwiftNodeUtils.node2RowIterator(node)));
        }
        return rowList;
    }

    private double[] getValues(Row row, CubeData cubeData) {
        double[] result = new double[cubeData.getMetrics().size()];
        int offset = cubeData.getDimensions().size();
        for (int i = 0; i < cubeData.getMetrics().size(); i++) {
            result[i] = row.getValue(i + offset);
        }
        return result;
    }

    private RowIndexKey<int[]> getKey(Row row, CubeData cubeData) {
        int dimensionSize = cubeData.getDimensions().size();
        int[] key = new int[dimensionSize];
        for (int i = 0; i < dimensionSize; i++) {
            key[i] = cubeData.getDimensions().get(i).getKey().getDictionaryEncodedColumn().getIndex(row.getValue(i));
        }
        return new RowIndexKey<>(key);
    }
}

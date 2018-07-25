package com.fr.swift.query.segment.group;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.query.group.by.CubeData;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.group.info.MetricInfoImpl;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.node.GroupNodeUtils;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.source.Row;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Lyon on 2018/5/31.
 */
public class GroupAllSegmentQueryTest {

    private GroupByInfo groupByInfo;
    private NodeMergeResultSet<GroupNode> resultSet;
    private CubeData cubeData;

    @Before
    public void setUp() throws Exception {

        cubeData = new CubeData();
        groupByInfo = new GroupByInfoImpl(cubeData.getDimensions(), new DetailFilter() {
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
        resultSet = (NodeMergeResultSet<GroupNode>) new GroupAllSegmentQuery(groupByInfo, metricInfo).getQueryResult();
        // 更新Node#data
        int dimensionSize = groupByInfo.getDimensions().size();
        GroupNodeUtils.updateNodeData(dimensionSize, ((GroupNode) resultSet.getNode()), resultSet.getRowGlobalDictionaries());
    }

    @Test
    public void test() {
        Map<RowIndexKey<int[]>, double[]> result = cubeData.getAggregationResult();
        try {
            while (resultSet.hasNext()) {
                Row row = resultSet.getNextRow();
                RowIndexKey<int[]> key = getKey(row);
                Assert.assertTrue(result.containsKey(key));
                double[] values = getValues(row);
                Assert.assertTrue(Arrays.equals(values, result.get(key)));
            }
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    private double[] getValues(Row row) {
        double[] result = new double[cubeData.getMetrics().size()];
        int offset = cubeData.getDimensions().size();
        for (int i = 0; i < cubeData.getMetrics().size(); i++) {
            result[i] = row.getValue(i + offset);
        }
        return result;
    }

    private RowIndexKey<int[]> getKey(Row row) {
        int dimensionSize = cubeData.getDimensions().size();
        int[] key = new int[dimensionSize];
        for (int i = 0; i < dimensionSize; i++) {
            key[i] = cubeData.getDimensions().get(i).getKey().getDictionaryEncodedColumn().getIndex(row.getValue(i));
        }
        return new RowIndexKey<>(key);
    }
}

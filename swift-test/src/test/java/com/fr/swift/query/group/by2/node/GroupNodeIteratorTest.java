package com.fr.swift.query.group.by2.node;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.query.group.by.CubeData;
import com.fr.swift.query.group.by2.DFTIterator;
import com.fr.swift.query.group.by2.ItCreator;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.MetricInfoImpl;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.result.row.RowIndexKey;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/7.
 */
public class GroupNodeIteratorTest extends TestCase {

    private GroupByInfo groupByInfo;
    private Iterator<GroupNode> iterator;
    private CubeData cubeData;

    @Override
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
        RowMapper rowMapper = new RowMapper(new MetricInfoImpl(cubeData.getMetrics(), cubeData.getAggregators(), cubeData.getAggregators().size()));
        ItemMapper itemMapper = new ItemMapper(groupByInfo.getDimensions());
        int dimensionSize = groupByInfo.getDimensions().size();
        iterator = new GroupNodeIterator(dimensionSize, Integer.MAX_VALUE,
                new DFTIterator(dimensionSize, new ItCreator(groupByInfo)), itemMapper, rowMapper);
    }

    public void test() {
        assertTrue(iterator.hasNext());
        Iterator<List<SwiftNode>> rows = SwiftNodeUtils.node2RowListIterator(iterator.next());
        assertFalse(iterator.hasNext());
        Map<RowIndexKey<int[]>, double[]> result = cubeData.getAggregationResult();
        while (rows.hasNext()) {
            List<SwiftNode> row = rows.next();
            RowIndexKey<int[]> key = getKey(row);
            Assert.assertTrue(result.containsKey(key));
            double[] values = getValues(row);
            Assert.assertTrue(Arrays.equals(values, result.get(key)));
        }
    }

    private double[] getValues(List<SwiftNode> row) {
        AggregatorValue[] values = row.get(row.size() - 1).getAggregatorValue();
        double[] result = new double[values.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = values[i].calculate();
        }
        return result;
    }

    private RowIndexKey<int[]> getKey(List<SwiftNode> row) {
        int[] key = new int[row.size()];
        for (int i = 0; i < key.length; i++) {
            key[i] = ((GroupNode) row.get(i)).getDictionaryIndex();
        }
        return new RowIndexKey<>(key);
    }
}

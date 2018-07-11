package com.fr.swift.query.group.by2.node;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.query.group.by.CubeData;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by2.node.iterator.GroupNodeIterator;
import com.fr.swift.query.group.by2.node.mapper.GroupNodeRowMapper;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.MetricInfoImpl;
import com.fr.swift.query.group.info.cursor.ExpanderImpl;
import com.fr.swift.query.group.info.cursor.ExpanderType;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.test.TestIo;
import com.fr.swift.util.function.BiFunction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/7.
 */
public class GroupNodeIteratorTest extends TestIo {

    private GroupByInfo groupByInfo;
    private Iterator<GroupNode[]> iterator;
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
        }, new ArrayList<>(), new ExpanderImpl(ExpanderType.ALL_EXPANDER, new HashSet<>()), null);
        GroupNodeRowMapper rowMapper = new GroupNodeRowMapper(new MetricInfoImpl(cubeData.getMetrics(), cubeData.getAggregators(), cubeData.getAggregators().size()));
        BiFunction<Integer, GroupByEntry, GroupNode> itemMapper = new BiFunction<Integer, GroupByEntry, GroupNode>() {
            @Override
            public GroupNode apply(Integer deep, GroupByEntry groupByEntry) {
                // 这边先存segment的字典序号吧
                return new GroupNode((int) deep, groupByEntry.getIndex());
            }
        };
        iterator = new GroupNodeIterator<GroupNode>(groupByInfo, new GroupNode(-1, null), itemMapper, rowMapper);
    }

    @Test
    public void test() {
        Map<RowIndexKey<int[]>, double[]> result = cubeData.getAggregationResult();
        while (iterator.hasNext()) {
            GroupNode[] row = iterator.next();
            RowIndexKey<int[]> key = getKey(row);
            Assert.assertTrue(result.containsKey(key));
            double[] values = getValues(row);
            Assert.assertTrue(Arrays.equals(values, result.get(key)));
        }
    }

    private double[] getValues(GroupNode[] row) {
        AggregatorValue[] values = row[row.length - 1].getAggregatorValue();
        double[] result = new double[values.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = values[i].calculate();
        }
        return result;
    }

    private RowIndexKey<int[]> getKey(GroupNode[] row) {
        int[] key = new int[row.length];
        for (int i = 0; i < key.length; i++) {
            key[i] = row[i].getDictionaryIndex();
        }
        return new RowIndexKey<>(key);
    }
}

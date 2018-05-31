package com.fr.swift.query.group.by2.row;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.query.group.by.CubeData;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.cursor.ExpanderImpl;
import com.fr.swift.query.group.info.cursor.ExpanderType;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.function.Function;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/7.
 */
public class MultiGroupByRowIteratorTest extends TestCase {

    private GroupByInfo groupByInfo;
    private Iterator<GroupByEntry[]> iterator;
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
        }, new ArrayList<>(), new ExpanderImpl(ExpanderType.ALL_EXPANDER, new HashSet<>()));
        iterator = new MultiGroupByRowIterator(groupByInfo);
    }

    public void test() {
        Map<RowIndexKey<int[]>, double[]> result = cubeData.getAggregationResult();
        GroupByEntryMapper mapper = new GroupByEntryMapper(cubeData.getAggregators(), cubeData.getMetrics());
        while (iterator.hasNext()) {
            GroupByEntry[] entries = iterator.next();
            Pair<RowIndexKey<int[]>, double[]> row = mapper.apply(entries);
            assertTrue(result.containsKey(row.getKey()));
            assertTrue(Arrays.equals(result.get(row.getKey()), row.getValue()));
        }
    }

    private static class GroupByEntryMapper implements Function<GroupByEntry[], Pair<RowIndexKey<int[]>, double[]>> {

        private List<Aggregator> aggregatorList;
        private List<Column> metrics;

        public GroupByEntryMapper(List<Aggregator> aggregatorList, List<Column> metrics) {
            this.aggregatorList = aggregatorList;
            this.metrics = metrics;
        }

        @Override
        public Pair<RowIndexKey<int[]>, double[]> apply(GroupByEntry[] p) {
            int[] key = new int[p.length];
            double[] values = new double[metrics.size()];
            for (int i = 0; i < key.length; i++) {
                key[i] = p[i].getIndex();
            }
            for (int i = 0; i < metrics.size(); i++) {
                // p[p.length - 1]对应当前行的明细行号
                values[i] = aggregatorList.get(i).aggregate(p[p.length - 1].getTraversal(), metrics.get(i)).calculate();
            }
            return Pair.of(new RowIndexKey<>(key), values);
        }
    }
}

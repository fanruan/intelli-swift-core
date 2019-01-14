package com.fr.swift.query.group.by2.node;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.by.CubeData;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.group.info.MetricInfoImpl;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeQRS;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2019/1/4.
 */
public class NodeGroupByUtilsTest extends TestCase {

    private Iterator<NodeMergeQRS<GroupNode>> iterator;
    private List<Pair<Column, IndexInfo>> dimensions;
    private Column metric;
    private Map<List<String>, Double> expected;
    private int rowCount = 10000;

    @Override
    public void setUp() throws Exception {
        CubeData cubeData = new CubeData(2, 1, rowCount);
        dimensions = cubeData.getDimensions();
        metric = cubeData.getMetrics().get(0);
        DetailFilter filter = EasyMock.createMock(DetailFilter.class);
        EasyMock.expect(filter.createFilterIndex()).andReturn(BitMaps.newAllShowBitMap(rowCount)).anyTimes();
        EasyMock.replay(filter);
        GroupByInfo groupByInfo = new GroupByInfoImpl(Integer.MAX_VALUE, dimensions, filter, new ArrayList<Sort>(), null);
        MetricInfo metricInfo = new MetricInfoImpl(Collections.singletonList(metric),
                Collections.singletonList(AggregatorFactory.createAggregator(AggregatorType.SUM)), 1);
        iterator = NodeGroupByUtils.groupBy(groupByInfo, metricInfo);
        prepareExpected();
    }

    private void prepareExpected() {
        expected = new HashMap<List<String>, Double>();
        for (int i = 0; i < rowCount; i++) {
            List<String> keys = new ArrayList<String>();
            for (int j = 0; j < dimensions.size(); j++) {
                String value = (String) dimensions.get(j).getKey().getDetailColumn().get(i);
                keys.add(value);
            }
            Double value = expected.get(keys);
            Integer detail = (Integer) metric.getDetailColumn().get(i);
            if (detail != null) {
                value = value == null ? detail : value + detail;
            }
            expected.put(keys, value);
        }
    }

    public void test() {
        assertTrue(iterator.hasNext());
        GroupNode root = iterator.next().getPage().getKey();
        assertNotNull(root);
        Iterator<List<SwiftNode>> it = SwiftNodeUtils.node2RowListIterator(root);
        assertTrue(it.hasNext());
        while (it.hasNext()) {
            List<SwiftNode> row = it.next();
            List<String> key = getKey(row);
            assertTrue(expected.containsKey(key));
            assertEquals(expected.get(key), row.get(row.size() - 1).getAggregatorValue()[0].calculateValue());
        }
    }

    private List<String> getKey(List<SwiftNode> row) {
        List<String> key = new ArrayList<String>();
        for (int i = 0; i < row.size(); i++) {
            key.add((String) row.get(i).getData());
        }
        return key;
    }
}
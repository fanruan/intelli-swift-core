package com.fr.swift.query.group.by2.node;

import com.fr.swift.SwiftContext;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.index.TestCubeData;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.filter.detail.impl.AllShowDetailFilter;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.group.info.MetricInfoImpl;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.test.Preparer;
import junit.framework.TestCase;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Lyon on 2018/5/7.
 */
public class GroupNodeIteratorTest extends TestCase {

    private List<Row> actualRows;
    private Set<Row> expected;

    @Before
    public void setUp() throws Exception {
        Preparer.prepareCubeBuild(GroupNodeIteratorTest.class);
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "test");
        TestCubeData.prepare(dataSource, GroupNodeIteratorTest.class);
        Segment segment = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class)
                .getSegment(dataSource.getSourceKey()).get(0);
        List<Pair<Column, IndexInfo>> dimensions = TestCubeData.getDimensions(segment, Arrays.asList("合同类型", "合同付款类型"));
        List<Column> metrics = TestCubeData.getColumns(segment, Collections.singletonList("购买数量"));
        GroupByInfo groupByInfo = new GroupByInfoImpl(Integer.MAX_VALUE, dimensions, new AllShowDetailFilter(segment),
                new ArrayList<>(), null);
        MetricInfo metricInfo = new MetricInfoImpl(metrics,
                Collections.singletonList(AggregatorFactory.createAggregator(AggregatorType.SUM)), metrics.size());
        SwiftNode root = NodeGroupByUtils.groupBy(groupByInfo, metricInfo).next().getPage().getKey();
        actualRows = IteratorUtils.iterator2List(SwiftNodeUtils.node2RowIterator(root));
        Map<RowIndexKey<Object[]>, ImmutableBitMap> map = TestCubeData.groupBy(Arrays.asList(
                segment.getColumn(new ColumnKey("合同类型")).getDetailColumn(),
                segment.getColumn(new ColumnKey("合同付款类型")).getDetailColumn()),
                segment.getAllShowIndex());
        expected = TestCubeData.aggregate(map, metrics, metricInfo.getAggregators());
    }

    public void test() {
        assertEquals(expected.size(), actualRows.size());
        for (Row actual : actualRows) {
            assertTrue(expected.contains(actual));
        }
    }
}

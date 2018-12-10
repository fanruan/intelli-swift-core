package com.fr.swift.query.group.by2.node;

import com.fr.swift.source.Row;
import junit.framework.TestCase;
import org.junit.Before;

import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/5/7.
 */
public class GroupNodeIteratorTest extends TestCase {

    private List<Row> actualRows;
    private Set<Row> expected;

    @Before
    public void setUp() throws Exception {
//        Preparer.prepareCubeBuild(GroupNodeIteratorTest.class);
//        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "test");
//        TestCubeData.prepare(dataSource, GroupNodeIteratorTest.class);
//        Segment segment = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class)
//                .getSegment(dataSource.getSourceKey()).get(0);
//        List<Pair<Column, IndexInfo>> dimensions = TestCubeData.getDimensions(segment, Arrays.asList("合同类型", "合同付款类型"));
//        List<Column> metrics = TestCubeData.getColumns(segment, Collections.singletonList("购买数量"));
//        GroupByInfo groupByInfo = new GroupByInfoImpl(Integer.MAX_VALUE, dimensions, new AllShowDetailFilter(segment),
//                new ArrayList<>(), null);
//        MetricInfo metricInfo = new MetricInfoImpl(metrics,
//                Collections.singletonList(AggregatorFactory.createAggregator(AggregatorType.SUM)), metrics.size());
//        SwiftNode root = NodeGroupByUtils.groupBy(groupByInfo, metricInfo).next().getPage().getKey();
//        actualRows = IteratorUtils.iterator2List(SwiftNodeUtils.node2RowIterator(root));
//        Map<RowIndexKey<Object[]>, ImmutableBitMap> map = TestCubeData.groupBy(Arrays.asList(
//                segment.getColumn(new ColumnKey("合同类型")).getDetailColumn(),
//                segment.getColumn(new ColumnKey("合同付款类型")).getDetailColumn()),
//                segment.getAllShowIndex());
//        expected = TestCubeData.aggregate(map, metrics, metricInfo.getAggregators());
    }

    public void test() {
        assertEquals(expected.size(), actualRows.size());
        for (Row actual : actualRows) {
            assertTrue(expected.contains(actual));
        }
    }
}

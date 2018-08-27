package com.fr.swift.data.operator.merger;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.generate.ColumnDictMerger;
import com.fr.swift.generate.ColumnIndexer;
import com.fr.swift.generate.TestIndexer;
import com.fr.swift.generate.history.transport.TableTransporter;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.HistorySegment;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.merge.RealtimeMerger;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.test.Preparer;
import com.fr.swift.util.DataSourceUtils;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class created on 2018/4/24
 *
 * @author Lucifer
 * @description 增量更新新增块合并成历史块
 * @since Advanced FineBI 5.0
 */
public class SwiftMergerTest extends BaseTest {

    private LocalSegmentProvider segmentProvider;
    private DataSource dataSource;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Preparer.prepareCubeBuild(getClass());
        dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "SwiftMergerTest");
        segmentProvider = SwiftContext.get().getBean(LocalSegmentProvider.class);
    }

    @Test
    public void testSingleSegmentMerge() throws Exception {
        //先做全量更新
        TableTransporter tableTransporter = new TableTransporter(dataSource);
        tableTransporter.transport();
        TestIndexer.historyIndex(dataSource, tableTransporter);
        List<Segment> segmentList = segmentProvider.getSegment(dataSource.getSourceKey());
        assertEquals(segmentList.size(), 1);
        assertTrue(segmentList.get(0) instanceof HistorySegmentImpl);
        assertEquals(segmentList.get(0).getRowCount(), 668);
        assertTrue(segmentList.get(0).getAllShowIndex().contains(0));
        assertTrue(segmentList.get(0).getAllShowIndex().contains(667));
        assertFalse(segmentList.get(0).getAllShowIndex().contains(668));

        //增量更新1
        ((Inserter) SwiftContext.get().getBean("incrementer", dataSource)).insertData(SwiftSourceTransferFactory.createSourceTransfer(
                new QueryDBSource("select * from DEMO_CONTRACT where 合同类型 ='购买合同'", "SwiftMergerTest")).createResultSet());

        segmentList = segmentProvider.getSegment(dataSource.getSourceKey());
        assertEquals(segmentList.size(), 2);
        //判断第一块内容没有改变
        assertTrue(segmentList.get(0) instanceof HistorySegmentImpl);
        assertEquals(segmentList.get(0).getRowCount(), 668);
        assertTrue(segmentList.get(0).getAllShowIndex().contains(0));
        assertTrue(segmentList.get(0).getAllShowIndex().contains(667));
        assertFalse(segmentList.get(0).getAllShowIndex().contains(668));
        //判断第二块新增内容
        assertTrue(segmentList.get(1) instanceof RealTimeSegmentImpl);
        DetailColumn column = (segmentList.get(1).getColumn(new ColumnKey("合同类型")).getDetailColumn());
        assertEquals(segmentList.get(1).getRowCount(), 482);
        for (int i = 0; i < 482; i++) {
            assertEquals(column.get(i), "购买合同");
        }

        //增量更新1
        ((Inserter) SwiftContext.get().getBean("incrementer", dataSource)).insertData(
                SwiftSourceTransferFactory.createSourceTransfer(
                        new QueryDBSource("select * from DEMO_CONTRACT where 合同类型 ='长期协议'", "SwiftMergerTest")).createResultSet());

        segmentList = segmentProvider.getSegment(dataSource.getSourceKey());
        assertEquals(segmentList.size(), 3);
        //判断第二块没有改变
        assertTrue(segmentList.get(1) instanceof RealTimeSegmentImpl);
        assertEquals(segmentList.get(1).getRowCount(), 482);
        column = (segmentList.get(1).getColumn(new ColumnKey("合同类型")).getDetailColumn());
        assertEquals(segmentList.get(1).getRowCount(), 482);
        for (int i = 0; i < 482; i++) {
            assertEquals(column.get(i), "购买合同");
        }
        //判断第三块新增内容
        assertTrue(segmentList.get(2) instanceof RealTimeSegmentImpl);
        column = (segmentList.get(2).getColumn(new ColumnKey("合同类型")).getDetailColumn());
        assertEquals(segmentList.get(2).getRowCount(), 49);
        for (int i = 0; i < 49; i++) {
            assertEquals(column.get(i), "长期协议");
        }

        RealtimeMerger realtimeMerger = new RealtimeMerger(dataSource.getSourceKey(),
                dataSource.getMetadata(), DataSourceUtils.getSwiftSourceKey(dataSource).getId());
        //合并前是不为空
        assertFalse(ResourceDiscovery.getInstance().isCubeResourceEmpty());
        realtimeMerger.merge();
        //合并清除为空
        assertTrue(ResourceDiscovery.getInstance().isCubeResourceEmpty());


        segmentList = segmentProvider.getSegment(dataSource.getSourceKey());

        for (int i = 1; i <= dataSource.getMetadata().getColumnCount(); i++) {
            ColumnIndexer columnIndexer = new ColumnIndexer(dataSource, new ColumnKey(dataSource.getMetadata().getColumnName(i)),
                    Collections.singletonList(segmentList.get(1)));
            columnIndexer.work();
            ColumnDictMerger merger = new ColumnDictMerger(dataSource, new ColumnKey(dataSource.getMetadata().getColumnName(i)),
                    segmentList);
            merger.work();
        }

        assertEquals(segmentList.size(), 2);
        //判断第一块没变
        assertTrue(segmentList.get(0) instanceof HistorySegment);
        assertEquals(segmentList.get(0).getRowCount(), 668);

        //判断第二块是原第三四块内容
        assertTrue(segmentList.get(1) instanceof HistorySegment);
        column = (segmentList.get(1).getColumn(new ColumnKey("合同类型")).getDetailColumn());
        assertEquals(segmentList.get(1).getRowCount(), 531);
        for (int i = 0; i < 482; i++) {
            assertEquals(column.get(i), "购买合同");
        }
        for (int i = 482; i < 531; i++) {
            assertEquals(column.get(i), "长期协议");
        }
    }
}

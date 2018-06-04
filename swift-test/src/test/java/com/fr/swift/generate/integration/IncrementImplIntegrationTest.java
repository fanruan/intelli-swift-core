package com.fr.swift.generate.integration;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.flow.FlowRuleController;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.generate.TestIndexer;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.generate.history.transport.TableTransporter;
import com.fr.swift.generate.realtime.RealtimeDataTransporter;
import com.fr.swift.increase.IncrementImpl;
import com.fr.swift.increment.Increment;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import org.junit.Test;

import java.util.List;

/**
 * This class created on 2018-1-11 09:45:45
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 * 增量更新集成测试
 */
public class IncrementImplIntegrationTest extends BaseTest {

    private final LocalSegmentProvider segmentProvider = SwiftContext.getInstance().getBean(LocalSegmentProvider.class);
    private DataSource dataSource;

    @Override
    public void setUp() throws Exception {
        super.setUp();
//        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir") + "\\" + System.currentTimeMillis()));
        TestConnectionProvider.createConnection();

        dataSource = new QueryDBSource("select 记录人 from DEMO_CAPITAL_RETURN", "local2");
    }

    @Test
    public void testIntegration() throws Exception {
        //先做全量更新
        TableTransporter tableTransporter = new TableTransporter(dataSource);
        tableTransporter.transport();
        List<Segment> segments = segmentProvider.getSegment(dataSource.getSourceKey());

        ColumnIndexer columnIndexer = new ColumnIndexer(dataSource, new ColumnKey("记录人"), segments);
        columnIndexer.work();
        List<Segment> segmentList = segmentProvider.getSegment(dataSource.getSourceKey());
        assertEquals(segmentList.size(), 1);
        assertTrue(segmentList.get(0) instanceof HistorySegmentImpl);
        assertEquals(segmentList.get(0).getRowCount(), 682);
        assertTrue(segmentList.get(0).getAllShowIndex().contains(0));
        assertTrue(segmentList.get(0).getAllShowIndex().contains(681));
        assertFalse(segmentList.get(0).getAllShowIndex().contains(682));

        //再做增量新增'庆芳'更新
        Increment increment = new IncrementImpl("select 记录人 from DEMO_CAPITAL_RETURN where 记录人 ='庆芳'", null, null, dataSource.getSourceKey(), "local");

        RealtimeDataTransporter transport = new RealtimeDataTransporter(dataSource, increment, new FlowRuleController());
        transport.work();
        TestIndexer.realtimeIndex(dataSource);

        segmentList = segmentProvider.getSegment(dataSource.getSourceKey());
        assertEquals(segmentList.size(), 2);
        //判断第一块内容没有改变
        assertTrue(segmentList.get(0) instanceof HistorySegmentImpl);
        assertEquals(segmentList.get(0).getRowCount(), 682);
        assertTrue(segmentList.get(0).getAllShowIndex().contains(0));
        assertTrue(segmentList.get(0).getAllShowIndex().contains(681));
        assertFalse(segmentList.get(0).getAllShowIndex().contains(682));
        //判断第二块新增内容
        assertTrue(segmentList.get(1) instanceof RealTimeSegmentImpl);
        DetailColumn column = (segmentList.get(1).getColumn(new ColumnKey("记录人")).getDetailColumn());
        assertEquals(segmentList.get(1).getRowCount(), 32);
        assertEquals(column.get(0), "庆芳");
        assertEquals(column.get(31), "庆芳");
        try {
            column.get(32);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        for (int i = 0; i < 32; i++) {
            assertTrue(segmentList.get(1).getAllShowIndex().contains(i));
        }
        assertFalse(segmentList.get(1).getAllShowIndex().contains(32));
        BitmapIndexedColumn bitmapIndexedColumn = (segmentList.get(1).getColumn(new ColumnKey("记录人")).getBitmapIndex());
        DictionaryEncodedColumn dictionaryEncodedColumn = (segmentList.get(1).getColumn(new ColumnKey("记录人")).getDictionaryEncodedColumn());
        int count = 1;
        try {
            while (bitmapIndexedColumn.getBitMapIndex(count) != null) {
                ImmutableBitMap bitMap = bitmapIndexedColumn.getBitMapIndex(count);
                String value = dictionaryEncodedColumn.getValue(count).toString();
                bitMap.traversal(row -> {
                    assertEquals(value, column.get(row));
                });
                count++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            assertTrue(true);
        }
        assertTrue(true);

        //最后做增量删除'庆芳'更新
        Increment increment2 = new IncrementImpl(null, "select 记录人 from DEMO_CAPITAL_RETURN where 记录人 ='庆芳'", null, dataSource.getSourceKey(), "local");
        RealtimeDataTransporter transport2 = new RealtimeDataTransporter(dataSource, increment2);
        transport2.work();

        segmentList = segmentProvider.getSegment(dataSource.getSourceKey());
        //判断第一块数据不变，但是allshowindex去掉了庆芳的索引
        assertTrue(segmentList.get(0) instanceof HistorySegmentImpl);
        assertEquals(segmentList.get(0).getRowCount(), 682);
        int showNumber = 0;
        int allNumber = 0;
        for (int i = 0; i < 682; i++) {
            if (segmentList.get(0).getAllShowIndex().contains(i)) {
                showNumber++;
            }
            if (segmentList.get(0).getColumn(new ColumnKey("记录人")).getDetailColumn().get(i) != null) {
                allNumber++;
            }
        }
        assertEquals(showNumber, 650);
        assertEquals(allNumber, 682);
        //判断第二块数据不变，但是allshowindex已经为空了。
        assertTrue(segmentList.get(1) instanceof RealTimeSegmentImpl);
        assertEquals(segmentList.get(1).getRowCount(), 32);
        int showNumber2 = 0;
        int allNumber2 = 0;
        for (int i = 0; i < 32; i++) {
            if (segmentList.get(1).getAllShowIndex().contains(i)) {
                showNumber2++;
            }
            if (segmentList.get(1).getColumn(new ColumnKey("记录人")).getDetailColumn().get(i) != null) {
                allNumber2++;
            }
        }
        assertEquals(showNumber2, 0);
        assertEquals(allNumber2, 32);
    }
}

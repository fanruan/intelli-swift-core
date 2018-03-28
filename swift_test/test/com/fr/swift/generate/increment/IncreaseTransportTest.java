package com.fr.swift.generate.increment;

import com.fr.swift.flow.FlowRuleController;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.generate.TestIndexer;
import com.fr.swift.generate.realtime.RealtimeDataTransporter;
import com.fr.swift.increase.IncrementImpl;
import com.fr.swift.increment.Increment;
import com.fr.swift.manager.LocalSegmentProvider;
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
 * This class created on 2018-1-5 15:31:46
 *
 * @author Lucifer
 * @description todo String类型数据未处理
 * @since Advanced FineBI Analysis 1.0
 */
public class IncreaseTransportTest extends BaseTest {

    private DataSource dataSource;

    @Override
    public void setUp() throws Exception {
        super.setUp();
//        new LocalSwiftServerService().start();
//        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir") + "\\" + System.currentTimeMillis()));
        TestConnectionProvider.createConnection();
        dataSource = new QueryDBSource("select 付款时间 from DEMO_CAPITAL_RETURN", "IncreaseTest");
    }

    @Test
    public void testIncreaseTransport() {
        try {
            Increment increment = new IncrementImpl("select 付款时间 from DEMO_CAPITAL_RETURN where 记录人 ='庆芳'", null, null, dataSource.getSourceKey(), "IncreaseTest");

            RealtimeDataTransporter transport = new RealtimeDataTransporter(dataSource, increment, new FlowRuleController());
            transport.work();
            TestIndexer.realtimeIndex(dataSource);

            List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
            Segment segment = segments.get(0);
            assertTrue(segment instanceof RealTimeSegmentImpl);
            DetailColumn column = (segment.getColumn(new ColumnKey("付款时间")).getDetailColumn());
            assertEquals(segment.getRowCount(), 32);
            assertTrue(column.get(0) != null);
            assertTrue(column.get(31) != null);
            try {
                column.get(32);
                assertTrue(false);
            } catch (Exception e) {
                assertTrue(true);
            }

            for (int i = 0; i < 32; i++) {
                assertTrue(segment.getAllShowIndex().contains(i));
            }
            assertFalse(segment.getAllShowIndex().contains(32));


            BitmapIndexedColumn bitmapIndexedColumn = (segment.getColumn(new ColumnKey("付款时间")).getBitmapIndex());
            DictionaryEncodedColumn dictionaryEncodedColumn = (segment.getColumn(new ColumnKey("付款时间")).getDictionaryEncodedColumn());

            for (int i = 0; i < segment.getRowCount(); i++) {
                assertNotNull(dictionaryEncodedColumn.getIndexByRow(i));
                assertNotNull(dictionaryEncodedColumn.getValue(dictionaryEncodedColumn.getIndexByRow(i)));
            }

            try {
                assertNull(dictionaryEncodedColumn.getIndexByRow(segment.getRowCount() + 1));
            } catch (ArrayIndexOutOfBoundsException EE) {
                assertTrue(true);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

}

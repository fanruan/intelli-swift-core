package com.fr.swift.generate.flow;

import com.fr.base.FRContext;
import com.fr.dav.LocalEnv;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.flow.FlowControlRule;
import com.fr.swift.flow.FlowRuleController;
import com.fr.swift.flow.RowNumberControlRule;
import com.fr.swift.flow.TimeControlRule;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.generate.realtime.RealtimeDataTransporter;
import com.fr.swift.increase.IncrementImpl;
import com.fr.swift.increment.Increment;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-15 16:35:14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class IncreaseFlowControlTest extends TestCase {

    private DataSource dataSource;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir") + "\\" + System.currentTimeMillis()));
        TestConnectionProvider.createConnection();
        dataSource = new QueryDBSource("select 合同ID from DEMO_CAPITAL_RETURN", "IncreaseFlowControlTest");
    }


    @Test
    public void testRowControl() throws Exception{
        Increment increment = new IncrementImpl("select 合同ID from DEMO_CAPITAL_RETURN where 记录人 ='庆芳'", null, null, dataSource.getSourceKey(), "local");

        /**
         * 控制增量只取5行
         */
        FlowControlRule flowControlRule = new RowNumberControlRule(5);
        List<FlowControlRule> list = new ArrayList<FlowControlRule>();
        list.add(flowControlRule);
        FlowRuleController flowRuleController = new FlowRuleController(list);
        RealtimeDataTransporter transport = new RealtimeDataTransporter(dataSource, increment, flowRuleController);
        transport.work();

        for (int i = 1; i <= dataSource.getMetadata().getColumnCount(); i++) {
            ColumnIndexer columnIndexer = new ColumnIndexer(dataSource, new ColumnKey(dataSource.getMetadata().getColumnName(i)));
            columnIndexer.work();
        }

        List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        Segment segment = segments.get(0);

        DetailColumn column = (segment.getColumn(new ColumnKey("合同ID")).getDetailColumn());
        assertEquals(segment.getRowCount(), 5);
        assertTrue(column.get(0) != null);
        assertTrue(column.get(4) != null);
        try {
            column.get(5);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        for (int i = 0; i < 5; i++) {
            assertTrue(segment.getAllShowIndex().contains(i));
        }
        assertFalse(segment.getAllShowIndex().contains(5));


        BitmapIndexedColumn bitmapIndexedColumn = (segment.getColumn(new ColumnKey("合同ID")).getBitmapIndex());
        DictionaryEncodedColumn dictionaryEncodedColumn = (segment.getColumn(new ColumnKey("合同ID")).getDictionaryEncodedColumn());

        int count = 0;
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
    }

    @Test
    public void testTimeControl() {
        Increment increment = new IncrementImpl("select * from DEMO_CAPITAL_RETURN where 记录人 ='庆芳'", null, null, dataSource.getSourceKey(), "local");

        FlowControlRule flowControlRule = new TimeControlRule(100);
        List<FlowControlRule> list = new ArrayList<FlowControlRule>();
        list.add(flowControlRule);
        FlowRuleController flowRuleController = new FlowRuleController(list);
        RealtimeDataTransporter transport = new RealtimeDataTransporter(dataSource, increment, flowRuleController);
        transport.work();

        List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        Segment segment = segments.get(0);

        DetailColumn column = (segment.getColumn(new ColumnKey("合同ID")).getDetailColumn());
        assertTrue(segment.getRowCount() < 30);
        assertTrue(segment.getRowCount() > 0);
    }
}

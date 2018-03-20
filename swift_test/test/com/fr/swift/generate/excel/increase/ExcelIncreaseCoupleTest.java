package com.fr.swift.generate.excel.increase;

import com.fr.swift.flow.FlowRuleController;
import com.fr.swift.generate.excel.BaseExcelTest;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.generate.history.transport.TableTransporter;
import com.fr.swift.generate.realtime.RealtimeColumnIndexer;
import com.fr.swift.generate.realtime.RealtimeDataTransporter;
import com.fr.swift.increase.IncrementImpl;
import com.fr.swift.increment.Increment;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.HistorySegment;
import com.fr.swift.segment.RealTimeSegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.excel.ExcelDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/3/19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ExcelIncreaseCoupleTest extends BaseExcelTest {

    public void testIncreaseCoupleFiles() throws Exception {
        dataSource = new ExcelDataSource(path2, names, types);
        TableTransporter tableTransporter = new TableTransporter(dataSource);
        tableTransporter.transport();

        for (int i = 1; i <= dataSource.getMetadata().getColumnCount(); i++) {
            ColumnIndexer columnIndexer = new ColumnIndexer(dataSource, new ColumnKey(dataSource.getMetadata().getColumnName(i)));
            columnIndexer.work();
        }
        List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        Segment segment = segments.get(0);
        assertEquals(segment.getRowCount(), 3);
        assertEquals(segments.size(), 1);

        List<String> list = new ArrayList<>();
        list.add(path1);
        list.add(path3);

        Increment increment = new IncrementImpl(dataSource.getColumnNames(), dataSource.getColumnTypes(), list);

        RealtimeDataTransporter transport = new RealtimeDataTransporter(dataSource, increment, new FlowRuleController());
        transport.work();

        for (int i = 1; i <= dataSource.getMetadata().getColumnCount(); i++) {
            RealtimeColumnIndexer<?> indexer = new RealtimeColumnIndexer(dataSource, new ColumnKey(dataSource.getMetadata().getColumnName(i)));
            indexer.work();
        }

        segments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        assertEquals(segments.size(), 2);

        assertEquals(segments.get(0).getRowCount(), 3);
        assertEquals(segments.get(0).getColumn(new ColumnKey("A")).getDetailColumn().get(0), "a11");
        assertEquals(segments.get(0).getColumn(new ColumnKey("A")).getDetailColumn().get(1), "a12");
        assertEquals(segments.get(0).getColumn(new ColumnKey("A")).getDetailColumn().get(2), "a13");
        assertTrue(segments.get(0) instanceof HistorySegment);


        assertEquals(segments.get(1).getRowCount(), 6);
        assertTrue(segments.get(1) instanceof RealTimeSegment);
        assertEquals(segments.get(1).getColumn(new ColumnKey("A")).getDetailColumn().get(0), "a1");
        assertEquals(segments.get(1).getColumn(new ColumnKey("A")).getDetailColumn().get(1), "a2");
        assertEquals(segments.get(1).getColumn(new ColumnKey("A")).getDetailColumn().get(2), "a3");
        assertEquals(segments.get(1).getColumn(new ColumnKey("A")).getDetailColumn().get(3), "a21");
        assertEquals(segments.get(1).getColumn(new ColumnKey("A")).getDetailColumn().get(4), "a22");
        assertEquals(segments.get(1).getColumn(new ColumnKey("A")).getDetailColumn().get(5), "a23");
    }
}

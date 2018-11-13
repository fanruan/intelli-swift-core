package com.fr.swift.generate.excel.increase;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.generate.TestIndexer;
import com.fr.swift.generate.excel.BaseExcelTest;
import com.fr.swift.generate.history.transport.TableTransporter;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.HistorySegment;
import com.fr.swift.segment.RealTimeSegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.excel.ExcelDataSource;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * This class created on 2018/3/19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ExcelIncreSingleTest extends BaseExcelTest {

    @Test
    public void testIncreaseOneFile() throws Exception {
        dataSource = new ExcelDataSource(path1, names, types);
        TableTransporter tableTransporter = new TableTransporter(dataSource);
        tableTransporter.transport();

        List<Segment> segments = SwiftContext.get().getBean(LocalSegmentProvider.class).getSegment(dataSource.getSourceKey());
        TestIndexer.historyIndex(dataSource, tableTransporter);

        Segment segment = segments.get(0);
        assertEquals(segment.getRowCount(), 3);
        assertEquals(segments.size(), 1);

        DataSource dataSource2 = new ExcelDataSource(path2, names, types);

        ((Inserter) SwiftContext.get().getBean("incrementer", dataSource)).insertData(SwiftSourceTransferFactory.createSourceTransfer(dataSource2).createResultSet());

        segments = SwiftContext.get().getBean(LocalSegmentProvider.class).getSegment(dataSource.getSourceKey());
        assertEquals(segments.size(), 2);

        assertEquals(segments.get(0).getRowCount(), 3);
        assertEquals(segments.get(0).getColumn(new ColumnKey("A")).getDetailColumn().get(0), "a1");
        assertEquals(segments.get(0).getColumn(new ColumnKey("A")).getDetailColumn().get(1), "a2");
        assertEquals(segments.get(0).getColumn(new ColumnKey("A")).getDetailColumn().get(2), "a3");
        assertTrue(segments.get(0) instanceof HistorySegment);


        assertEquals(segments.get(1).getRowCount(), 3);
        assertTrue(segments.get(1) instanceof RealTimeSegment);
        assertEquals(segments.get(1).getColumn(new ColumnKey("A")).getDetailColumn().get(0), "a11");
        assertEquals(segments.get(1).getColumn(new ColumnKey("A")).getDetailColumn().get(1), "a12");
        assertEquals(segments.get(1).getColumn(new ColumnKey("A")).getDetailColumn().get(2), "a13");
    }
}
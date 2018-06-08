package com.fr.swift.generate.excel.all;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.generate.TestIndexer;
import com.fr.swift.generate.TestTransport;
import com.fr.swift.generate.excel.BaseExcelTest;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.excel.ExcelDataSource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This class created on 2018/3/19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ExcelGenerateTest extends BaseExcelTest {

    private LocalSegmentProvider segmentProvider;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        segmentProvider = SwiftContext.getInstance().getBean(LocalSegmentProvider.class);
    }

    @Test
    public void testSingleFileGenerateExcel() throws Exception {
        dataSource = new ExcelDataSource(path1, names, types);

        TestIndexer.historyIndex(dataSource, TestTransport.historyTransport(dataSource));

        List<Segment> segments = segmentProvider.getSegment(dataSource.getSourceKey());
        Segment segment = segments.get(0);
        assertEquals(segment.getRowCount(), 3);
        ImmutableBitMap bitMap = segment.getAllShowIndex();

        int containCount = 0;
        for (int i = 0; i < 3; i++) {
            if (bitMap.contains(i)) {
                containCount++;
            }
        }
        assertEquals(containCount, 3);
    }

    @Test
    public void testCoupleFilesGenerateExcel() throws Exception {
        List<String> appendPaths = new ArrayList<>();
        appendPaths.add(path2);
        appendPaths.add(path3);
        dataSource = new ExcelDataSource(path1, names, types, appendPaths);

        TestIndexer.historyIndex(dataSource, TestTransport.historyTransport(dataSource));

        List<Segment> segments = segmentProvider.getSegment(dataSource.getSourceKey());
        Segment segment = segments.get(0);
        assertEquals(segment.getRowCount(), 9);
        ImmutableBitMap bitMap = segment.getAllShowIndex();

        int containCount = 0;
        for (int i = 0; i < 9; i++) {
            if (bitMap.contains(i)) {
                containCount++;
            }
        }
        assertEquals(containCount, 9);

        assertEquals(segment.getColumn(new ColumnKey("A")).getDetailColumn().get(0), "a1");
        assertEquals(segment.getColumn(new ColumnKey("A")).getDetailColumn().get(1), "a2");
        assertEquals(segment.getColumn(new ColumnKey("A")).getDetailColumn().get(2), "a3");

        assertEquals(segment.getColumn(new ColumnKey("A")).getDetailColumn().get(3), "a11");
        assertEquals(segment.getColumn(new ColumnKey("A")).getDetailColumn().get(4), "a12");
        assertEquals(segment.getColumn(new ColumnKey("A")).getDetailColumn().get(5), "a13");

        assertEquals(segment.getColumn(new ColumnKey("A")).getDetailColumn().get(6), "a21");
        assertEquals(segment.getColumn(new ColumnKey("A")).getDetailColumn().get(7), "a22");
        assertEquals(segment.getColumn(new ColumnKey("A")).getDetailColumn().get(8), "a23");
    }
}

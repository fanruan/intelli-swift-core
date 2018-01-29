package com.fr.swift.fine.adaptor.executor;

import com.fr.swift.source.excel.ExcelDataSource;
import junit.framework.TestCase;

/**
 * This class created on 2018-1-3 10:01:22
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftEXCELDataModelTest extends TestCase {
    private ExcelDataSource dataSource;
    private String path;
    private String[] names = new String[2];
    private int[] types = new int[2];

//    public void setUp() throws Exception {
//        path = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test.xlsx");
//        names[0] = "A";
//        names[1] = "B";
//        types[0] = ColumnTypeConstants.COLUMN.STRING;
//        types[1] = ColumnTypeConstants.COLUMN.NUMBER;
//        dataSource = new ExcelDataSource(path, names, types);
//    }
//
//
//    public void testSwiftEXCELDataModelGetFields() throws Exception {
//        SwiftEXCELEngineExcutor dataModel = new SwiftEXCELEngineExcutor();
//        List<FineBusinessField> list = dataModel.getFieldList(path, names, types, null);
//        assertEquals(list.size(), 2);
//        assertTrue(true);
//    }
//
//    public void testSwiftEXCELDataModelPreviewDBTable() throws Exception {
//        SwiftEXCELEngineExcutor dataModel = new SwiftEXCELEngineExcutor();
//        BIDetailTableResult detailTableResult = dataModel.getPreviewData(path, names, types, new ArrayList<String>(), 100);
//        assertEquals(detailTableResult.columnSize(), 2);
//        int count = 0;
//        while (detailTableResult.hasNext()) {
//            List<BIDetailCell> cellList = detailTableResult.next();
//            assertEquals(cellList.size(), 2);
//            count++;
//        }
//        assertEquals(count, 3);
//        assertTrue(true);
//    }
}

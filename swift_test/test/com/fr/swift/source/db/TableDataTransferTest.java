package com.fr.swift.source.db;

import com.fr.swift.resource.ResourceUtils;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.excel.ExcelDataSource;
import com.fr.swift.source.excel.ExcelTableData;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2017/12/29.
 */
public class TableDataTransferTest extends TestCase {


    public void testCreateResultSet() throws Exception{
        String path = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test.xlsx");
        String[] names = {"A", "B"};
        int[] types = {ColumnTypeConstants.COLUMN.STRING, ColumnTypeConstants.COLUMN.NUMBER};
        ExcelDataSource source = new ExcelDataSource(path, names, types);
        TableDataTransfer excelTransfer = new TableDataTransfer(new ExcelTableData(path, names, types), source.getMetadata(), source.getOuterMetadata());
        SwiftResultSet resultSet = excelTransfer.createResultSet();
        List<Row> list = new ArrayList<Row>();
        while (resultSet.next()){
            list.add(resultSet.getRowData());
        }
        resultSet.close();
        assertEquals(list.size(), 3);
    }

    public void testCreatePartResultSet() throws Exception{
        String path = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test.xlsx");
        String[] names = {"A", "B"};
        int[] types = {ColumnTypeConstants.COLUMN.STRING, ColumnTypeConstants.COLUMN.NUMBER};
        Map<String, Integer> fields = new HashMap<>();
        fields.put("B", ColumnTypeConstants.COLUMN.STRING);
        ExcelDataSource source = new ExcelDataSource(path, names, types, fields);
        TableDataTransfer excelTransfer = new TableDataTransfer(new ExcelTableData(path, names, types), source.getMetadata(), source.getOuterMetadata());
        SwiftResultSet resultSet = excelTransfer.createResultSet();
        List<Row> list = new ArrayList<Row>();
        while (resultSet.next()){
            list.add(resultSet.getRowData());
        }
        resultSet.close();
        assertEquals(list.size(), 3);
    }
}
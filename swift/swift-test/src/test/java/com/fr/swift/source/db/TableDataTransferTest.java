package com.fr.swift.source.db;

import com.fr.swift.resource.ResourceUtils;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.Row;
import com.fr.swift.source.excel.ExcelDataSource;
import com.fr.swift.source.excel.ExcelTableData;
import com.fr.workspace.simple.SimpleWork;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by pony on 2017/12/29.
 */
public class TableDataTransferTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SimpleWork.checkIn(ResourceUtils.class.getClassLoader().getResource("").getPath());
    }

    @Override
    public void tearDown() {
        SimpleWork.checkOut();
    }

    public void testCreateResultSet() throws Exception{
        String path = ResourceUtils.getFileAbsolutePath("excel/test.xlsx");
        String[] names = {"A", "B"};
        ColumnType[] types = {ColumnType.STRING, ColumnType.NUMBER};
        ExcelDataSource source = new ExcelDataSource(path, names, types);
        TableDataTransfer excelTransfer = new TableDataTransfer(new ExcelTableData(path, names, types), source.getMetadata(), source.getOuterMetadata());
        SwiftResultSet resultSet = excelTransfer.createResultSet();
        List<Row> list = new ArrayList<Row>();
        while (resultSet.hasNext()) {
            list.add(resultSet.getNextRow());
        }
        resultSet.close();
        assertEquals(list.size(), 3);
    }

    public void testCreatePartResultSet() throws Exception{
        String path = ResourceUtils.getFileAbsolutePath("excel/test.xlsx");
        String[] names = {"A", "B"};
        ColumnType[] types = {ColumnType.STRING, ColumnType.NUMBER};
        LinkedHashMap<String, ColumnType> fields = new LinkedHashMap<>();
        fields.put("B", ColumnType.STRING);
        ExcelDataSource source = new ExcelDataSource(path, names, types, fields);
        TableDataTransfer excelTransfer = new TableDataTransfer(new ExcelTableData(path, names, types), source.getMetadata(), source.getOuterMetadata());
        SwiftResultSet resultSet = excelTransfer.createResultSet();
        List<Row> list = new ArrayList<Row>();
        while (resultSet.hasNext()) {
            list.add(resultSet.getNextRow());
        }
        resultSet.close();
        assertEquals(list.size(), 3);
    }
}
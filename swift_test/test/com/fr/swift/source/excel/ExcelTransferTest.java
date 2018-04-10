package com.fr.swift.source.excel;

import com.fr.swift.resource.ResourceUtils;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by pony on 2017/12/28.
 */
public class ExcelTransferTest extends TestCase {
    @Test
    public void testCreateResultSet() throws Exception {
        List<String> paths = new ArrayList<String>();
        String path = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test.xlsx");
        paths.add(path);
        paths.add(ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test1.xlsx"));
        paths.add(ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test2.xlsx"));
        String[] names = {"A", "B"};
        ColumnType[] types = {ColumnType.STRING, ColumnType.NUMBER};
        ExcelDataSource source = new ExcelDataSource(path, names, types);
        ExcelTransfer excelTransfer = new ExcelTransfer(paths, source.getMetadata(), source.getOuterMetadata());
        SwiftResultSet resultSet = excelTransfer.createResultSet();
        List<Row> list = new ArrayList<Row>();
        while (resultSet.next()) {
            list.add(resultSet.getRowData());
        }
        resultSet.close();
        assertEquals(list.size(), 9);
        LinkedHashMap<String, ColumnType> fields = new LinkedHashMap<>();
        fields.put("B", ColumnType.STRING);
        ExcelDataSource partSource = new ExcelDataSource(path, names, types, fields);
        ExcelTransfer excelPartTransfer = new ExcelTransfer(paths, partSource.getMetadata(), partSource.getOuterMetadata());
        SwiftResultSet partResultSet = excelPartTransfer.createResultSet();
        partResultSet.next();
        //如果第二个位置取到值了，就错了
        try {
            Object ob = partResultSet.getRowData().getValue(0);
            assertEquals(ob.getClass(), String.class);
            partResultSet.getRowData().getValue(1);
            assert false;
        } catch (IndexOutOfBoundsException e) {
            assert true;
        }
    }
}
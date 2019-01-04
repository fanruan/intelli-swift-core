package com.fr.swift.source.excel;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.Row;
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
//        String path = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test.xlsx");
        paths.add(ExcelInfo.getUrl2003());
        paths.add(ExcelInfo.getUrl2007());
//        paths.add(ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test1.xlsx"));
//        paths.add(ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test2.xlsx"));
        String[] names = ExcelInfo.getColumnNames();
        ColumnType[] types = ExcelInfo.getColumnTypes();
        ExcelDataSource source = new ExcelDataSource(ExcelInfo.getUrl2003(), names, types);
        ExcelTransfer excelTransfer = new ExcelTransfer(paths, source.getMetadata(), source.getOuterMetadata());
        SwiftResultSet resultSet = excelTransfer.createResultSet();
        List<Row> list = new ArrayList<Row>();
        while (resultSet.hasNext()) {
            list.add(resultSet.getNextRow());
        }
        resultSet.close();
        assertEquals(list.size(), 20000);
        LinkedHashMap<String, ColumnType> fields = new LinkedHashMap<String, ColumnType>();
        fields.put("zssalegp", ColumnType.STRING);
        ExcelDataSource partSource = new ExcelDataSource(ExcelInfo.getUrl2003(), names, types, fields);
        ExcelTransfer excelPartTransfer = new ExcelTransfer(paths, partSource.getMetadata(), partSource.getOuterMetadata());
        SwiftResultSet partResultSet = excelPartTransfer.createResultSet();
        partResultSet.hasNext();
        //如果第二个位置取到值了，就错了
        try {
            Object ob = partResultSet.getNextRow().getValue(0);
            assertEquals(ob.getClass(), String.class);
            partResultSet.getNextRow().getValue(1);
            assert false;
        } catch (IndexOutOfBoundsException e) {
            assert true;
        }
    }
}
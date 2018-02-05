package com.fr.swift.source.excel;

import com.fr.swift.resource.ResourceUtils;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import junit.framework.TestCase;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pony on 2017/12/28.
 */
public class ExcelDataSourceTest extends TestCase {
    private ExcelDataSource dataSource;
    private ExcelDataSource partSource;

    public void setUp() throws Exception {
        String path = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/excel/test.xlsx");
        String[] names = {"A", "B"};
        int[] types = {ColumnTypeConstants.COLUMN.STRING, ColumnTypeConstants.COLUMN.NUMBER};
        dataSource = new ExcelDataSource(path, names, types);
        Map<String, Integer> fields = new HashMap<>();
        fields.put("B", ColumnTypeConstants.COLUMN.STRING);
        partSource = new ExcelDataSource(path, names, types, fields);
    }

    public void testGetSourceKey() throws Exception{
        SourceKey key = dataSource.getSourceKey();
        assertEquals(key.getId(), "f05f42ea");
    }

    public void testGetMetadata() throws Exception{
        SwiftMetaData metaData = dataSource.getMetadata();
        assertEquals(metaData.getColumnCount(), 2);
        assertEquals(metaData.getColumnName(1), "A");
        assertEquals(metaData.getColumnName(2), "B");
        assertEquals(metaData.getColumnType(1), Types.VARCHAR);
        assertEquals(metaData.getColumnType(2), Types.DOUBLE);
        SwiftMetaData partMetaData = partSource.getMetadata();
        assertEquals(partMetaData.getColumnCount(), 1);
        assertEquals(partMetaData.getColumnName(1), "B");
        assertEquals(partMetaData.getColumnType(1), Types.VARCHAR);

    }
}
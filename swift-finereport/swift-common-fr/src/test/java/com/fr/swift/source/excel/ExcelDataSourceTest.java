package com.fr.swift.source.excel;

import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.SwiftMetaData;
import junit.framework.TestCase;

import java.sql.Types;
import java.util.LinkedHashMap;

/**
 * Created by pony on 2017/12/28.
 */
public class ExcelDataSourceTest extends TestCase {
    private ExcelDataSource dataSource;
    private ExcelDataSource partSource;

    public void setUp() throws Exception {
        dataSource = new ExcelDataSource(ExcelInfo.getUrl2007(), ExcelInfo.getColumnNames(), ExcelInfo.getColumnTypes());
        LinkedHashMap<String, ColumnType> fields = new LinkedHashMap<String, ColumnType>();
        fields.put("zssalegp", ColumnType.STRING);
        partSource = new ExcelDataSource(ExcelInfo.getUrl2007(), ExcelInfo.getColumnNames(), ExcelInfo.getColumnTypes(), fields);
    }

    public void testGetMetadata() throws Exception {
        SwiftMetaData metaData = dataSource.getMetadata();
        assertEquals(metaData.getColumnCount(), 9);
        String[] columnNames = ExcelInfo.getColumnNames();
        int[] sqlType = ExcelInfo.getSqlType();
        for (int i = 1; i <= columnNames.length; i++) {
            assertEquals(metaData.getColumnName(i), columnNames[i - 1]);
            assertEquals(metaData.getColumnType(i), sqlType[i - 1]);
        }

        SwiftMetaData partMetaData = partSource.getMetadata();
        assertEquals(partMetaData.getColumnCount(), 1);
        assertEquals(partMetaData.getColumnName(1), "zssalegp");
        assertEquals(partMetaData.getColumnType(1), Types.VARCHAR);

    }
}
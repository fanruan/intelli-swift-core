package com.fr.swift.source.excel;

import com.fr.swift.source.ColumnTypeConstants;

import java.sql.Types;

/**
 * @author yee
 * @date 2018/4/27
 */
public class ExcelInfo {
    private static String[] columnNames = new String[]{
            "0calday", "zsproduct", "zscustmr", "zssalegp", "zsprice", "0currency", "zsqty", "0unit", "zssales"
    };
    private static String url2003 = "http://resin.zyee.me:8080/1w.xls";
    private static String url2007 = "http://resin.zyee.me:8080/1w.xlsx";
    private static String urlcsv = "http://resin.zyee.me:8080/1w.csv";

    private static int[] sqlType = {
            Types.DOUBLE,
            Types.VARCHAR,
            Types.VARCHAR,
            Types.VARCHAR,
            Types.DOUBLE,
            Types.VARCHAR,
            Types.DOUBLE,
            Types.VARCHAR,
            Types.VARCHAR
    };

    private static ColumnTypeConstants.ColumnType[] columnTypes = {
            ColumnTypeConstants.ColumnType.NUMBER,
            ColumnTypeConstants.ColumnType.STRING,
            ColumnTypeConstants.ColumnType.STRING,
            ColumnTypeConstants.ColumnType.STRING,
            ColumnTypeConstants.ColumnType.NUMBER,
            ColumnTypeConstants.ColumnType.STRING,
            ColumnTypeConstants.ColumnType.NUMBER,
            ColumnTypeConstants.ColumnType.STRING,
            ColumnTypeConstants.ColumnType.STRING
    };

    public static String[] getColumnNames() {
        return columnNames;
    }

    public static String getUrl2003() {
        return url2003;
    }

    public static String getUrl2007() {
        return url2007;
    }

    public static String getUrlcsv() {
        return urlcsv;
    }

    public static int[] getSqlType() {
        return sqlType;
    }

    public static ColumnTypeConstants.ColumnType[] getColumnTypes() {
        return columnTypes;
    }
}

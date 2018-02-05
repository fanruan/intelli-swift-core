package com.fr.swift.source;

import com.fr.swift.setting.PerformancePlugManager;

import java.sql.Types;

/**
 * Created by pony on 2017/12/13.
 * column的各种类型
 * columnType 对外展示的类型 NUMBER，DATE，STRING
 * classType java对象的类型，用来存储文件
 * sqlType 数据库标准的类型
 */
public class ColumnTypeUtils {
    private static final int MAX_LONG_COLUMN_SIZE = 19;

    /**
     * 通过数据库类型判断java类
     *
     * @param sqlType     数据库类型
     * @param scale
     * @param columnSize
     * @return java类型
     */
    public static int sqlTypeToClassType(int sqlType, int columnSize, int scale) {
        switch (sqlType) {
            case java.sql.Types.DECIMAL:
            case java.sql.Types.NUMERIC:
            case java.sql.Types.REAL:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.FLOAT:
                return getClassTypeByColumn(columnSize, scale);
            case java.sql.Types.BIT:
            case java.sql.Types.TINYINT:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.INTEGER:
                return ColumnTypeConstants.CLASS.INTEGER;
            case java.sql.Types.BIGINT:
                return ColumnTypeConstants.CLASS.LONG;
            case java.sql.Types.DATE:
            case java.sql.Types.TIMESTAMP:
            case java.sql.Types.TIME:
                return ColumnTypeConstants.CLASS.DATE;
            default:
                return ColumnTypeConstants.CLASS.STRING;
        }
    }

    //数值类型的需要根据长度小数点判断
    private static int getClassTypeByColumn(int columnSize, int scale) {
        if (scale == 0) {
            //没有小数点走整型判断逻辑
            return getClassTypeByColumnSize(columnSize);
        } else {
            //有小数点一定是double
            return ColumnTypeConstants.CLASS.DOUBLE;
        }
    }

    /**
     * 19个长度的整形读成long值 以上的读成字符串
     *
     * @param columnSize
     * @return
     */
    private static int getClassTypeByColumnSize(int columnSize) {
        if (isLongType(columnSize)) {
            return ColumnTypeConstants.CLASS.LONG;
        } else {
            return ColumnTypeConstants.CLASS.STRING;
        }
    }

    public static boolean isLongType(int columnSize) {
        return columnSize < MAX_LONG_COLUMN_SIZE || PerformancePlugManager.getInstance().isUseNumberType();
    }

    /**
     *  字段类型转sql类型
     * @param columnType
     * @return
     */
    public static int columnTypeToSqlType(int columnType) {
        switch (columnType) {
            case ColumnTypeConstants.COLUMN.NUMBER:
                return java.sql.Types.DOUBLE;

            case ColumnTypeConstants.COLUMN.DATE:
                return java.sql.Types.DATE;

            default:
                return java.sql.Types.VARCHAR;

        }
    }

    /**
     *  字段类型转sql类型
     * @param columnType
     * @return
     */
    public static SwiftMetaDataColumn convertColumn(int columnType, SwiftMetaDataColumn fromColumn) {
        int fromColumnType = sqlTypeToColumnType(fromColumn.getType(), fromColumn.getPrecision(), fromColumn.getScale());
        switch (columnType) {
            case ColumnTypeConstants.COLUMN.NUMBER:
                //Date → Number 是Long类型
                if (fromColumnType == ColumnTypeConstants.COLUMN.DATE){
                    return new MetaDataColumn(fromColumn.getName(), fromColumn.getRemark(), Types.BIGINT, MAX_LONG_COLUMN_SIZE - 1,0);
                }
                //文本转数值，scale设置为1，因为文本获取不到小数位数，如果是0的话，可能被是别成long，实际文本又有小数点，会丢精度。
                return new MetaDataColumn(fromColumn.getName(), fromColumn.getRemark(), Types.DOUBLE, fromColumn.getPrecision(), 1);
            case ColumnTypeConstants.COLUMN.DATE:
                return new MetaDataColumn(fromColumn.getName(), fromColumn.getRemark(), Types.DATE, fromColumn.getPrecision(), fromColumn.getScale());
            default:
                return new MetaDataColumn(fromColumn.getName(), fromColumn.getRemark(), Types.VARCHAR, fromColumn.getPrecision(), fromColumn.getScale());

        }
    }

    public static int classTypeToColumnType(int classType) {
        switch (classType) {
            case ColumnTypeConstants.CLASS.DATE:
                return ColumnTypeConstants.COLUMN.DATE;
            case ColumnTypeConstants.CLASS.INTEGER:
            case ColumnTypeConstants.CLASS.LONG:
            case ColumnTypeConstants.CLASS.DOUBLE:
                return ColumnTypeConstants.COLUMN.NUMBER;
            default: {
                return ColumnTypeConstants.COLUMN.STRING;
            }
        }
    }

    /**
     * 通过数据库类型判断字段类型
     *
     * @param sqlType     数据库类型
     * @param scale
     * @param columnSize
     * @return 字段类型
     */
    public static int sqlTypeToColumnType(int sqlType, int columnSize, int scale) {
        return classTypeToColumnType(sqlTypeToClassType(sqlType, columnSize, scale));
    }
}

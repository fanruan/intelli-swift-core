package com.fr.swift.source;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.setting.PerformancePlugManager;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.util.Crasher;

import java.sql.Types;

/**
 * @author pony
 * @date 2017/12/13
 * <p>
 * column的各种类型
 * columnType 对外展示的类型 NUMBER，DATE，STRING
 * classType java对象的类型，用来存储文件
 * sqlType 数据库标准的类型
 */
public class ColumnTypeUtils {
    public static final int MAX_LONG_COLUMN_SIZE = 19;

    /**
     * 通过数据库类型判断java类
     *
     * @param sqlType    数据库类型
     * @param scale
     * @param columnSize
     * @return java类型
     */
    public static ClassType sqlTypeToClassType(int sqlType, int columnSize, int scale) {
        switch (sqlType) {
            case Types.DECIMAL:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.FLOAT:
                return getClassTypeByColumn(columnSize, scale);
            case Types.BIT:
            case Types.BOOLEAN:
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                return ClassType.INTEGER;
            case Types.BIGINT:
                return ClassType.LONG;
            case Types.DATE:
            case Types.TIMESTAMP:
            case Types.TIME:
                return ClassType.DATE;
            default:
                return ClassType.STRING;
        }
    }

    private static ClassType getClassTypeByColumn(int columnSize, int scale) {
        // 数值类型的需要根据长度小数点判断
        if (scale == 0) {
            // 没有小数点走整型判断逻辑
            return getClassTypeByColumnSize(columnSize);
        } else {
            //有小数点一定是double
            return ClassType.DOUBLE;
        }
    }

    /**
     * 19个长度的整形读成long值 以上的读成字符串
     *
     * @param columnSize
     * @return
     */
    private static ClassType getClassTypeByColumnSize(int columnSize) {
        if (isLongType(columnSize)) {
            return ClassType.LONG;
        } else {
            return ClassType.STRING;
        }
    }

    public static boolean isLongType(int columnSize) {
        return columnSize < MAX_LONG_COLUMN_SIZE || PerformancePlugManager.getInstance().isUseNumberType();
    }

    /**
     * 字段类型转sql类型
     *
     * @param columnType
     * @return
     */
    public static int columnTypeToSqlType(ColumnType columnType) {
        switch (columnType) {
            case NUMBER:
                return Types.DOUBLE;
            case DATE:
                return Types.DATE;
            default:
                return Types.VARCHAR;
        }
    }

    /**
     * 字段类型转sql类型
     *
     * @param columnType
     * @return
     */
    public static SwiftMetaDataColumn convertColumn(ColumnType columnType, SwiftMetaDataColumn fromColumn) {
        ColumnType fromColumnType = getColumnType(fromColumn);
        switch (columnType) {
            case NUMBER:
                //Date → Number 是Long类型
                if (fromColumnType == ColumnType.DATE) {
                    return new MetaDataColumnBean(fromColumn.getName(), fromColumn.getRemark(), Types.BIGINT, MAX_LONG_COLUMN_SIZE - 1, 0);
                }
                //文本转数值，scale设置为1，因为文本获取不到小数位数，如果是0的话，可能被是别成long，实际文本又有小数点，会丢精度。
                return new MetaDataColumnBean(fromColumn.getName(), fromColumn.getRemark(), Types.DOUBLE, fromColumn.getPrecision(), 1);
            case DATE:
                return new MetaDataColumnBean(fromColumn.getName(), fromColumn.getRemark(), Types.DATE, fromColumn.getPrecision(), fromColumn.getScale());
            default:
                return new MetaDataColumnBean(fromColumn.getName(), fromColumn.getRemark(), Types.VARCHAR, fromColumn.getPrecision(), fromColumn.getScale());

        }
    }

    public static ColumnType classTypeToColumnType(ClassType classType) {
        switch (classType) {
            case DATE:
                return ColumnType.DATE;
            case INTEGER:
            case LONG:
            case DOUBLE:
                return ColumnType.NUMBER;
            default:
                return ColumnType.STRING;
        }
    }

    /**
     * 通过数据库类型判断字段类型
     *
     * @param sqlType    数据库类型
     * @param scale
     * @param columnSize
     * @return 字段类型
     */
    public static ColumnType sqlTypeToColumnType(int sqlType, int columnSize, int scale) {
        return classTypeToColumnType(sqlTypeToClassType(sqlType, columnSize, scale));
    }

    public static boolean checkColumnType(SwiftMetaDataColumn column, ColumnType type) {
        if (null == column || getColumnType(column) != type) {
            return Crasher.crash("not " + type + " field");
        }
        return true;
    }

    public static ClassType getClassType(SwiftMetaDataColumn columnMeta) {
        return sqlTypeToClassType(
                columnMeta.getType(),
                columnMeta.getPrecision(),
                columnMeta.getScale()
        );
    }

    public static ColumnType getColumnType(SwiftMetaDataColumn columnMeta) {
        return classTypeToColumnType(getClassType(columnMeta));
    }
}

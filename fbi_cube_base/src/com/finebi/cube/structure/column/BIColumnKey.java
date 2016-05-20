package com.finebi.cube.structure.column;

import com.finebi.cube.structure.column.date.BIDateColumnTool;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.*;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public final class BIColumnKey implements BIKey {
    private final String columnName;
    private final String columnType;
    private final String columnSubType;
    private static Set<String> columnTypeSet = new HashSet<String>();
    private static Set<String> columnSubTypeSet = new HashSet<String>();
    private static Map<Integer, String> fieldType2ColumnType = new HashMap<Integer, String>();

    public static String STRING_COLUMN_TYPE = "_fineBI_string_column_";
    public static String LONG_COLUMN_TYPE = "_fineBI_long_column_";
    public static String DOUBLE_COLUMN_TYPE = "_fineBI_double_column_";
    public static String DATA_COLUMN_TYPE = "_fineBI_data_column_";
    public static String ROW_COLUMN_TYPE = "_fineBI_row_column_";

    public static Integer ROW_COLUMN = -1;

    public static String EMPTY_SUB_TYPE = "_fineBI_sub_empty";
    public static String DATA_SUB_TYPE_YEAR = "_fineBI_sub_year_column_";
    public static String DATA_SUB_TYPE_DAY = "_fineBI_sub_day_column_";
    public static String DATA_SUB_TYPE_MONTH = "_fineBI_sub_month_column_";
    public static String DATA_SUB_TYPE_SEASON = "_fineBI_sub_season_column_";
    public static String DATA_SUB_TYPE_WEEK = "_fineBI_sub_week_column_";
    public static String DATA_SUB_TYPE_YEAR_MONTH_DAY = "_fineBI_sub_year_month_day_column_";

    static {
        columnTypeSet.add(STRING_COLUMN_TYPE);
        columnTypeSet.add(LONG_COLUMN_TYPE);
        columnTypeSet.add(DOUBLE_COLUMN_TYPE);
        columnTypeSet.add(DATA_COLUMN_TYPE);
        columnTypeSet.add(ROW_COLUMN_TYPE);
        fieldType2ColumnType.put(DBConstant.COLUMN.DATE + DBConstant.CLASS.DATE, DATA_COLUMN_TYPE);
        fieldType2ColumnType.put(DBConstant.COLUMN.DATE + DBConstant.CLASS.TIME, DATA_COLUMN_TYPE);
        fieldType2ColumnType.put(DBConstant.COLUMN.DATE + DBConstant.CLASS.TIMESTAMP, DATA_COLUMN_TYPE);
        fieldType2ColumnType.put(DBConstant.COLUMN.STRING + DBConstant.CLASS.STRING, STRING_COLUMN_TYPE);
        fieldType2ColumnType.put(DBConstant.COLUMN.STRING + DBConstant.CLASS.BOOLEAN, STRING_COLUMN_TYPE);
        fieldType2ColumnType.put(DBConstant.COLUMN.NUMBER + DBConstant.CLASS.INTEGER, LONG_COLUMN_TYPE);
        fieldType2ColumnType.put(DBConstant.COLUMN.NUMBER + DBConstant.CLASS.LONG, LONG_COLUMN_TYPE);
        fieldType2ColumnType.put(DBConstant.COLUMN.NUMBER + DBConstant.CLASS.DOUBLE, DOUBLE_COLUMN_TYPE);
        fieldType2ColumnType.put(DBConstant.COLUMN.NUMBER + DBConstant.CLASS.FLOAT, DOUBLE_COLUMN_TYPE);
        fieldType2ColumnType.put(DBConstant.COLUMN.NUMBER + DBConstant.CLASS.DECIMAL, DOUBLE_COLUMN_TYPE);
        fieldType2ColumnType.put(DBConstant.COLUMN.NUMBER + DBConstant.CLASS.DECIMAL, DOUBLE_COLUMN_TYPE);
        fieldType2ColumnType.put(DBConstant.COLUMN.ROW + DBConstant.CLASS.ROW, ROW_COLUMN_TYPE);

        columnSubTypeSet.add(EMPTY_SUB_TYPE);
        columnSubTypeSet.add(DATA_SUB_TYPE_YEAR);
        columnSubTypeSet.add(DATA_SUB_TYPE_DAY);
        columnSubTypeSet.add(DATA_SUB_TYPE_MONTH);
        columnSubTypeSet.add(DATA_SUB_TYPE_SEASON);
        columnSubTypeSet.add(DATA_SUB_TYPE_WEEK);
        columnSubTypeSet.add(DATA_SUB_TYPE_YEAR_MONTH_DAY);


    }


    private BIColumnKey(BICubeFieldSource field) {
        this(field.getFieldName(), fieldType2ColumnType.get(field.getClassType() + field.getFieldType()), EMPTY_SUB_TYPE);
    }

    public BIColumnKey(String columnName, String columnType, String columnSubType) {
        if (!columnTypeSet.contains(columnType) || !columnSubTypeSet.contains(columnSubType)) {
            throw BINonValueUtils.beyondControl();
        }
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnSubType = columnSubType;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getFullName() {
        return columnName + "_" + columnSubType;
    }

    @Override
    public String getKey() {
        StringBuffer sb = new StringBuffer();
        return sb.append("Name").append(columnName).append("Type:").append(columnType).append("SubType:").append(columnSubType).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BIColumnKey)) return false;

        BIColumnKey that = (BIColumnKey) o;

        if (columnName != null ? !columnName.equals(that.columnName) : that.columnName != null) return false;
        if (columnType != null ? !columnType.equals(that.columnType) : that.columnType != null) return false;
        return !(columnSubType != null ? !columnSubType.equals(that.columnSubType) : that.columnSubType != null);

    }

    @Override
    public int hashCode() {
        int result = columnName != null ? columnName.hashCode() : 0;
        result = 31 * result + (columnType != null ? columnType.hashCode() : 0);
        result = 31 * result + (columnSubType != null ? columnSubType.hashCode() : 0);
        return result;
    }

    public static List<BIColumnKey> generateColumnKey(BICubeFieldSource field) {
        List<BIColumnKey> result;
        if (field.hasSubField()) {
            result = generateDateSubField(field);
        } else {
            result = new ArrayList<BIColumnKey>();
        }
        result.add(BIColumnKey.covertColumnKey(field));
        return result;
    }

    public static BIColumnKey covertColumnKey(BICubeFieldSource field) {
        return new BIColumnKey(field);
    }

    public static List<BIColumnKey> generateSubField(BICubeFieldSource field) {
        switch (field.getFieldType()) {
            case DBConstant.COLUMN.DATE:
                return generateDateSubField(field);
            default:
                throw BINonValueUtils.beyondControl();
        }
    }

    private static List<BIColumnKey> generateDateSubField(BICubeFieldSource field) {
        List<BIColumnKey> result = new ArrayList<BIColumnKey>();
        result.add(BIDateColumnTool.generateDay(field));
        result.add(BIDateColumnTool.generateMonth(field));
        result.add(BIDateColumnTool.generateSeason(field));
        result.add(BIDateColumnTool.generateWeek(field));
        result.add(BIDateColumnTool.generateYear(field));
        result.add(BIDateColumnTool.generateYearMonthDay(field));
        return result;

    }

}

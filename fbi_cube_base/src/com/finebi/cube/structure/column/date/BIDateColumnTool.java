package com.finebi.cube.structure.column.date;

import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.CubeFieldSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/4/1.
 *
 * @author Connery
 * @since 4.0
 */
public class BIDateColumnTool {
    public static final BIColumnKey generateYear(CubeFieldSource field) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            return new BIColumnKey(field.getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_YEAR);
        } else {
            throw BINonValueUtils.beyondControl();
        }

    }

    public static final BIColumnKey generateMonth(CubeFieldSource field) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            return new BIColumnKey(field.getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_MONTH);
        } else {
            throw BINonValueUtils.beyondControl();
        }

    }

    public static final BIColumnKey generateDay(CubeFieldSource field) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            return new BIColumnKey(field.getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_DAY);
        } else {
            throw BINonValueUtils.beyondControl();
        }

    }

    public static final BIColumnKey generateSeason(CubeFieldSource field) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            return new BIColumnKey(field.getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_SEASON);
        } else {
            throw BINonValueUtils.beyondControl();
        }

    }

    public static final BIColumnKey generateWeek(CubeFieldSource field) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            return new BIColumnKey(field.getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_WEEK);
        } else {
            throw BINonValueUtils.beyondControl();
        }

    }

    public static final BIColumnKey generateYearMonthDay(CubeFieldSource field) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            return new BIColumnKey(field.getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH_DAY);
        } else {
            throw BINonValueUtils.beyondControl();
        }

    }
}

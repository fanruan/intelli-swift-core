package com.finebi.cube.adapter;

import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.date.BIDateColumnTool;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * Created by Young's on 2016/4/23.
 */
public class BIColumnKeyAdapter {
    public static final BIColumnKey covert(ICubeFieldSource field, int type) {
        BIColumnKey columnKey;
        switch (type) {
            case BIReportConstant.GROUP.Y:
                columnKey = BIDateColumnTool.generateYear(field); break;
            case BIReportConstant.GROUP.M:
                columnKey = BIDateColumnTool.generateMonth(field); break;
            case BIReportConstant.GROUP.MD:
                columnKey = BIDateColumnTool.generateDay(field); break;
            case BIReportConstant.GROUP.D:
                columnKey = BIDateColumnTool.generateDay(field); break;
            case BIReportConstant.GROUP.S:
                columnKey = BIDateColumnTool.generateSeason(field); break;
            case BIReportConstant.GROUP.W:
                columnKey = BIDateColumnTool.generateWeek(field); break;
            case BIReportConstant.GROUP.YMD:
                columnKey = BIDateColumnTool.generateYearMonthDay(field); break;
            case BIReportConstant.GROUP.YMDHMS:
                columnKey = BIDateColumnTool.generateYearMonthDayHourMinuteSecond(field); break;
            case BIReportConstant.GROUP.YM:
                columnKey = BIDateColumnTool.generateYearMonth(field); break;
            case BIReportConstant.GROUP.YS:
                columnKey = BIDateColumnTool.generateYearSeason(field); break;
            case BIReportConstant.GROUP.YW:
                columnKey = BIDateColumnTool.generateYearWeekNumber(field); break;
            case BIReportConstant.GROUP.HOUR:
                columnKey = BIDateColumnTool.generateHour(field); break;
            case BIReportConstant.GROUP.SECOND:
                columnKey = BIDateColumnTool.generateSecond(field); break;
            case BIReportConstant.GROUP.MINUTE:
                columnKey = BIDateColumnTool.generateMinute(field); break;
            case BIReportConstant.GROUP.YMDH:
                columnKey = BIDateColumnTool.generateYearMonthDayHour(field); break;
            case BIReportConstant.GROUP.YMDHM:
                columnKey = BIDateColumnTool.generateYearMonthDayHourMinute(field); break;
            case BIReportConstant.GROUP.WEEK_COUNT:
                columnKey = BIDateColumnTool.generateWeekNumber(field); break;
            default:
                throw BINonValueUtils.beyondControl();
        }
        return columnKey;
    }


}

package com.fr.bi.stable.data.key.date;

import com.fr.bi.stable.constant.BIReportConstant;


/**
 * Created by 小灰灰 on 2016/1/6.
 */
public class BIDateValueFactory {
    public static BIDateValue createDateValue(int type, Number value) {
        switch (type) {
            case BIReportConstant.GROUP.Y:
                return new BIYearValue(value.intValue());
            case BIReportConstant.GROUP.M:
                return new BIMonthValue(value.intValue());
            case BIReportConstant.GROUP.S:
                return new BISeasonValue(value.intValue());
            case BIReportConstant.GROUP.W:
                return new BIWeekValue(value.intValue());
            case BIReportConstant.GROUP.HOUR:
                return new BIHourValue(value.intValue());
            case BIReportConstant.GROUP.MINUTE:
                return new BIMinuteValue(value.intValue());
            case BIReportConstant.GROUP.SECOND:
                return new BISecondValue(value.intValue());
            case BIReportConstant.GROUP.WEEK_COUNT:
                return new BIWeekNumberValue(value.intValue());
            case BIReportConstant.GROUP.MD:
            case BIReportConstant.GROUP.D:
                return new BIDayOfMonthValue(value.intValue());
            case BIReportConstant.GROUP.YMD:
                return new BIDayValue(value.longValue());
            case BIReportConstant.GROUP.YMDH:
                return new BIYearMonthDayHourValue(value.longValue());
            case BIReportConstant.GROUP.YMDHM:
                return new BIYearMonthDayHourMinuteValue(value.longValue());
            case BIReportConstant.GROUP.YMDHMS:
                return new BIYearMonthDayHourMinuteSecondValue(value.longValue());
            case BIReportConstant.GROUP.YM:
                return  new BIYearMonthValue(value.longValue());
            case BIReportConstant.GROUP.YW:
                return  new BIYearWeekNumberValue(value.longValue());
            case BIReportConstant.GROUP.YS:
                return  new BIYearSeasonValue(value.longValue());
        }
        return null;
    }
}
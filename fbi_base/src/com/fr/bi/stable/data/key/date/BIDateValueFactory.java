package com.fr.bi.stable.data.key.date;

import com.fr.bi.stable.constant.BIReportConstant;

/**
 * Created by 小灰灰 on 2016/1/6.
 */
public class BIDateValueFactory {
    public static BIDateValue createDateValue(int type, Object value) {
        switch (type) {
            case BIReportConstant.GROUP.Y:
                return new BIYearValue(((Number) value).intValue());
            case BIReportConstant.GROUP.M:
                return new BIMonthValue(((Number) value).intValue());
            case BIReportConstant.GROUP.S:
                return new BISeasonValue(((Number) value).intValue());
            case BIReportConstant.GROUP.W:
                return new BIWeekValue(((Number) value).intValue());
            // 年月日
            case BIReportConstant.GROUP.YMD: {
                return new BIDayValue(((Number) value).longValue());
            }
            // 周数
            case BIReportConstant.GROUP.WEEK_COUNT: {
                return new BIWeekCountValue(((Number) value).intValue());
            }
            // 时
            case BIReportConstant.GROUP.HOUR: {
                return new BIHourValue(((Number) value).intValue());
            }
            // 分
            case BIReportConstant.GROUP.MINUTE: {
                return new BIMinuteValue(((Number) value).intValue());
            }
            // 秒
            case BIReportConstant.GROUP.SECOND: {
                return new BISecondValue(((Number) value).intValue());
            }
            // 年季度
            case BIReportConstant.GROUP.YS: {
                return new BIYearSessionValue((String) value);
            }
            // 年月
            case BIReportConstant.GROUP.YM: {
                return new BIYearMonthValue((String) value);
            }
            // 年周数
            case BIReportConstant.GROUP.YW: {
                return new BIYearWeekCountValue((String) value);
            }
            // 年月日时
            case BIReportConstant.GROUP.YMDH: {
                return new BIYearMonthDayHourValue(((Number) value).longValue());
            }
            // 年月日时分
            case BIReportConstant.GROUP.YMDHM: {
                return new BIYearMonthDayHourMinuteValue(((Number) value).longValue());
            }
            // 年月日时秒
            case BIReportConstant.GROUP.YMDHMS: {
                return new BIYearMonthDayHourMinuteSecondValue(((Number) value).longValue());
            }

        }
        return null;
    }
}
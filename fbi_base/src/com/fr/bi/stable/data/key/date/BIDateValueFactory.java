package com.fr.bi.stable.data.key.date;

import com.fr.bi.stable.constant.BIReportConstant;

/**
 * Created by 小灰灰 on 2016/1/6.
 */
public class BIDateValueFactory {
    public static BIDateValue createDateValue(int type, Number value){
        switch (type){
            case BIReportConstant.GROUP.Y:
                return new BIYearValue(value.intValue());
            case BIReportConstant.GROUP.M:
                return new BIMonthValue(value.intValue());
            case BIReportConstant.GROUP.S:
                return new BISeasonValue(value.intValue());
            case BIReportConstant.GROUP.W:
                return new BIWeekValue(value.intValue());
            case BIReportConstant.GROUP.YMD :{
                return new BIDayValue(value.longValue());
            }
        }
        return null;
    }
}
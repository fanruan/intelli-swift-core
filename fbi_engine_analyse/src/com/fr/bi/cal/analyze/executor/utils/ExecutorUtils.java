package com.fr.bi.cal.analyze.executor.utils;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.Inter;
import com.fr.stable.StringUtils;

/**
 * Created by Young's on 2016/9/21.
 */
public class ExecutorUtils {
    static final String NONE_VALUE = StringUtils.EMPTY;

    public static Object formatExtremeSumValue(Object value, int numLevel) {
        if (value == null) {
            value = NONE_VALUE;
        } else if (value instanceof Double) {
            if (Double.isInfinite((Double) value)) {
                value = "N/0";
            } else if (Double.isNaN((Double) value)) {
                value = "NAN";
            } else {
                switch (numLevel) {
                    case BIReportConstant.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        value = (Double) value / 10000;
                        break;
                    case BIReportConstant.TARGET_STYLE.NUM_LEVEL.MILLION:
                        value = (Double) value / 1000000;
                        break;
                    case BIReportConstant.TARGET_STYLE.NUM_LEVEL.YI:
                        value = (Double) value / 100000000;
                        break;
                    case BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        value = (Double) value * 100;
                        break;
                }
            }
        }
        return value;
    }

    public static String formatLevelAndUnit(int level, String unit) {
        String levelAndUnit;
        switch (level) {
            case BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL:
                levelAndUnit = unit;
                break;
            case BIReportConstant.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                levelAndUnit = Inter.getLocText("BI-Wan") +  unit;
                break;
            case BIReportConstant.TARGET_STYLE.NUM_LEVEL.MILLION:
                levelAndUnit = Inter.getLocText("BI-Million") +  unit;
                break;
            case BIReportConstant.TARGET_STYLE.NUM_LEVEL.YI:
                levelAndUnit = Inter.getLocText("BI-YI") +  unit;
                break;
            case BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT:
                levelAndUnit = "%" +  unit;
                break;
            default:
                levelAndUnit = unit;
        }
        return levelAndUnit;
    }
}

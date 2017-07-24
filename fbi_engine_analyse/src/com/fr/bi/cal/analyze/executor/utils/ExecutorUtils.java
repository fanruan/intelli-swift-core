package com.fr.bi.cal.analyze.executor.utils;

import com.fr.base.CoreDecimalFormat;
import com.fr.base.Style;
import com.fr.base.background.ColorBackground;
import com.fr.base.core.StyleUtils;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.ComparatorUtils;
import com.fr.general.FRFont;
import com.fr.general.GeneralUtils;
import com.fr.general.Inter;
import com.fr.stable.Constants;
import com.fr.stable.StringUtils;

import java.text.DecimalFormat;

/**
 * Created by Young's on 2016/9/21.
 */
public class ExecutorUtils {
    static final String NONE_VALUE = StringUtils.EMPTY;
    static final int TEN_THOUSAND = 10000;
    static final int MILLION = 1000000;
    static final int YI = 100000000;

    public static Object formatExtremeSumValue(Object value, int numLevel) {
        if (value == null) {
            value = NONE_VALUE;
        } else if (value instanceof Double || value instanceof Long) {
            Number v = GeneralUtils.objectToNumber(value);
            value = v.doubleValue();
            //负无穷显示空 正无穷显示N/0
            if (((Double) value) == Double.NEGATIVE_INFINITY) {
                return NONE_VALUE;
            } else if (((Double) value) == Double.POSITIVE_INFINITY) {
                return "N/0";
            } else if (Double.isNaN((Double) value)) {
                return "NAN";
            } else {
                switch (numLevel) {
                    case BIReportConstant.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        value = (Double) value / TEN_THOUSAND;
                        break;
                    case BIReportConstant.TARGET_STYLE.NUM_LEVEL.MILLION:
                        value = (Double) value / MILLION;
                        break;
                    case BIReportConstant.TARGET_STYLE.NUM_LEVEL.YI:
                        value = (Double) value / YI;
                        break;
                    case BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        //在excel单元格格式中设置
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
                levelAndUnit = Inter.getLocText("BI-Basic_Wan") + unit;
                break;
            case BIReportConstant.TARGET_STYLE.NUM_LEVEL.MILLION:
                levelAndUnit = Inter.getLocText("BI-Basic_Million") + unit;
                break;
            case BIReportConstant.TARGET_STYLE.NUM_LEVEL.YI:
                levelAndUnit = Inter.getLocText("BI-Basic_Yi") + unit;
                break;
            case BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT:
                levelAndUnit = "%" + unit;
                break;
            default:
                levelAndUnit = unit;
        }
        return levelAndUnit;
    }

    public static DecimalFormat formatDecimalAndSeparator(Object v, int numLevel, int decimal, boolean separator) {
        StringBuilder result = new StringBuilder();
        switch (decimal) {
            case BIReportConstant.TARGET_STYLE.FORMAT.NORMAL:
                result = new StringBuilder(separator ? "#,##0.##" : "0.##");
                if ((v instanceof Double) && ((Double) v == ((Double) v).longValue())) {
                    result = new StringBuilder(separator ? "#,##0" : "#0");
                }
                break;
            default:
                result.append(separator ? "#,##0" : "0");
                for (int i = 0; i < decimal; i++) {
                    result.append(i == 0 ? ".0" : "0");
                }
                break;
        }
        if (numLevel == BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT) {
            result.append("%");
        }
        return new CoreDecimalFormat(new DecimalFormat(result.toString()), result.toString());
    }

    public static CBCell createCBCell(Object v, int rowIdx, int rowSpan, int columnIdx, int columnSpan, Style style) {
        if(ComparatorUtils.equals(v, "N/0")) {
            style = style.deriveHorizontalAlignment(Constants.RIGHT);
        }
        CBCell cell = new CBCell((v instanceof Double) && ((Double) v == ((Double) v).longValue()) ? ((Double) v).longValue() : v);
        cell.setRow(rowIdx);
        cell.setRowSpan(rowSpan);
        cell.setColumn(columnIdx);
        cell.setColumnSpan(columnSpan);
        cell.setStyle(style);
        return cell;
    }

}

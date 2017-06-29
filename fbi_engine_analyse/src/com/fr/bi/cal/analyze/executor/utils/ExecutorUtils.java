package com.fr.bi.cal.analyze.executor.utils;

import com.fr.base.CoreDecimalFormat;
import com.fr.base.Style;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.GeneralUtils;
import com.fr.general.Inter;
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
            if (Double.isInfinite((Double) value)) {
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

    public static DecimalFormat formatDecimalAndSeparator(int numLevel, int decimal, boolean separator) {
        StringBuilder result = new StringBuilder();
        switch (decimal) {
            case BIReportConstant.TARGET_STYLE.FORMAT.NORMAL:
                result = new StringBuilder(separator ? "#,###.##" : "#.##");
                break;
            case BIReportConstant.TARGET_STYLE.FORMAT.ONE2POINT:
                result = new StringBuilder(separator ? "#,###" : "#0");
            default:
                result.append(separator ? "#,###." : "#.");
                for (int i = 0; i < decimal; i++) {
                    result.append("0");
                }
                break;
        }
        if (numLevel == BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT) {
            result.append("%");
        }
        return new CoreDecimalFormat(new DecimalFormat(result.toString()), result.toString());
    }

    public static CBCell createCellWithOutStyle(Object v, int rowIdx, int rowSpan, int columnIdx, int columnSpan) {
        CBCell cell = new CBCell(v);
        cell.setRow(rowIdx);
        cell.setRowSpan(rowSpan);
        cell.setColumn(columnIdx);
        cell.setColumnSpan(columnSpan);
        //默认CellGUIAttr
        cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
        return cell;
    }

    public static CBCell createCell(Object v, int rowIdx, int rowSpan, int columnIdx, int columnSpan, Style style) {
        CBCell cell = createCellWithOutStyle(v, rowIdx, rowSpan, columnIdx, columnSpan);
        cell.setStyle(style.deriveTextStyle(Style.TEXTSTYLE_SINGLELINE));
        //默认CellGUIAttr
        return cell;
    }
}

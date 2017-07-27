package com.fr.bi.cal.analyze.executor.utils;

import com.fr.base.CoreDecimalFormat;
import com.fr.base.Style;
import com.fr.base.background.ColorBackground;
import com.fr.base.core.StyleUtils;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.general.*;
import com.fr.stable.Constants;
import com.fr.stable.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Young's on 2016/9/21.
 */
public class ExecutorUtils {
    static final String NONE_VALUE = StringUtils.EMPTY;
    static final String POSITIVE_INFINITY = "N/0";
    static final String NAN = "NaN";
    static final int TEN_THOUSAND = 10000;
    static final int MILLION = 1000000;
    static final int YI = 100000000;
    static final int WEEK_COUNT = 52;
    static final int MONTH_COUNT = 12;

    public static Object formatExtremeSumValue(Object value, int numLevel) {
        //数据库空值处理 负无穷转null
        value = BICollectionUtils.cubeValueToWebDisplay(value);
        if (value == null) {
            value = NONE_VALUE;
        } else if (value instanceof Double || value instanceof Long) {
            Number v = GeneralUtils.objectToNumber(value);
            value = v.doubleValue();
            if (((Double) value) == Double.POSITIVE_INFINITY) {
                return POSITIVE_INFINITY;
            } else if (Double.isNaN((Double) value)) {
                return NAN;
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
        if (ComparatorUtils.equals(v, POSITIVE_INFINITY)) {
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

    public static Object formatDateGroup(int type, String v) {
        if(GeneralUtils.string2Number(v) == null){
            return v;
        }
        switch (type) {
            case BIReportConstant.GROUP.YMD:
                return DateUtils.DATEFORMAT2.format(new Date(GeneralUtils.string2Number(v).longValue()));
            case BIReportConstant.GROUP.YS:
                return formatYSByDateFormat(new Date(GeneralUtils.string2Number(v).longValue()));
            case BIReportConstant.GROUP.YM:
                return new SimpleDateFormat("yyyy-MM").format(new Date(GeneralUtils.string2Number(v).longValue()));
            case BIReportConstant.GROUP.YW:
                return formatYWByDateFormat(new Date(GeneralUtils.string2Number(v).longValue()));
            case BIReportConstant.GROUP.YMDH:
                return new SimpleDateFormat("yyyy-MM-dd H").format(new Date(GeneralUtils.string2Number(v).longValue()));
            case BIReportConstant.GROUP.YMDHM:
                return new SimpleDateFormat("yyyy-MM-dd H:m").format(new Date(GeneralUtils.string2Number(v).longValue()));
            case BIReportConstant.GROUP.YMDHMS:
                return new SimpleDateFormat("yyyy-MM-dd H:m:s").format(new Date(GeneralUtils.string2Number(v).longValue()));
            default:
                return v;
        }
    }

    private static int getSeason(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        return month / 3 + 1;
    }

    private static String formatYSByDateFormat(Date date) {
        SimpleDateFormat formatter;
        int season = getSeason(date);
        formatter = new SimpleDateFormat("yyyy-" + season);
        return formatter.format(date);
    }

    private static int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekOfYear = c.get(Calendar.WEEK_OF_YEAR);
        int mouth = c.get(Calendar.MONTH);
        //如果月份是12月，且求出来的周数是第一周，说明该日期实质上是这一年的第53周，也是下一年的第一周
        if (mouth >= MONTH_COUNT - 1 && weekOfYear <= 1) {
            weekOfYear += WEEK_COUNT;
        }
        return weekOfYear;
    }

    private static String formatYWByDateFormat(Date date) {
        SimpleDateFormat formatter;
        int week = getWeekOfYear(date);
        formatter = new SimpleDateFormat("yyyy-" + week);
        return formatter.format(date);
    }
}

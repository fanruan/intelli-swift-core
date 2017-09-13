package com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.utils;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.bi.util.BIFormatHelper;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;

import java.text.DecimalFormat;

/**
 * Created by Kary on 2017/5/11.
 */
public class BITableCellFormatHelper {
    static final int DEFAULT_SCALE = 1;
    static final String NONE_VALUE = "--";

    //StableUtils.isNumber(text)的问题还在
    public static String targetValueFormat(JSONObject settings, String text) throws JSONException {
        try {
            if (Double.valueOf(text).isNaN()) {
                return text;
            }
            if (Double.valueOf(text).isInfinite()) {
                if (Double.valueOf(text) == Double.POSITIVE_INFINITY) {
                    return "∞";
                } else {
                    return "-∞";
                }
//                return "N/0";
            }
            double value = parseNumByLevel(settings, Double.valueOf(text));
            text = parseNumByFormat(decimalFormat(settings), value);
            String unit = scaleUnit(settings.optInt("numLevel"));
//            return text + unit;
            return text;
        } catch (NumberFormatException e) {
            return text;
        }
    }

    public static String headerTextFormat(JSONObject settings, String text) throws JSONException {
        try {
            String tail = createTailUnit(settings);
            if (StringUtils.isEmpty(tail)) {
                return text;
            } else {
                return text + BIStringUtils.append("(", tail, ")");
            }
        } catch (NumberFormatException e) {
            BILoggerFactory.getLogger(BITableCellFormatHelper.class).error(e.getMessage(), e);
        }
        return text;
    }

    private static String parseNumByFormat(String format, double value) {
        // 和web端处理一致，整数情况下不显示小数点位数
        if (Math.floor(value) == value && format.indexOf(".") > 0) {
            format = format.split("\\.")[0];
        }
        DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(value);
    }

    private static double parseNumByLevel(JSONObject setting, double value) {
        try {
            if (value == BIBaseConstant.NUMBER_VALUE.ZONE) {
                return value;
            }
            int numLevel = setting.optInt("numLevel", DEFAULT_SCALE);
            double scaleNum = BIFormatHelper.numberScaleByLevel(numLevel);
            value /= scaleNum;
        } catch (Exception e) {
            BILoggerFactory.getLogger(BITableCellFormatHelper.class).error(e.getMessage(), e);
        }
        return value;
    }

    public static String createTailUnit(JSONObject settings) {
        String scaleUnit = scaleUnit(settings.optInt("numLevel"));
        String unit = settings.optString("unit", StringUtils.EMPTY);
        return scaleUnit + unit;
    }

    public static String dateFormat(JSONObject format, int groupType, String text) throws JSONException {
        if (StringUtils.isBlank(text) || !StableUtils.isNumber(text)) {
            return text;
        }
        JSONObject dateFormat = format.optJSONObject("dateFormat");
        int dateFormatType = dateFormat == null ? BIReportConstant.DATE_FORMAT.SPLIT : dateFormat.optInt("type", BIReportConstant.DATE_FORMAT.SPLIT);
        switch (groupType) {
            case BIReportConstant.GROUP.YS:
                text = formatCombineDateByDateFormat(text, dateFormatType, new String[]{getLocText("BI-Basic_Year"), getLocText("BI-Basic_Quarter")});
                break;
            case BIReportConstant.GROUP.YM:
                text = formatCombineDateByDateFormat(text, dateFormatType, new String[]{getLocText("BI-Basic_Year"), getLocText("BI-Basic_Month")});
                break;
            case BIReportConstant.GROUP.YW:
                text = formatCombineDateByDateFormat(text, dateFormatType, new String[]{getLocText("BI-Basic_Year"), getLocText("BI-Week_Simple")});
                break;
            default:
                text = BIFormatHelper.dateFormat(dateFormat, groupType, text);
                break;
        }
        return text;
    }


    private static String getLocText(String key) {
        return Inter.getLocText(key);
    }

    private static String formatCombineDateByDateFormat(String category, int dateFormatType, String[] format) {

        if (dateFormatType == BIReportConstant.DATE_FORMAT.CHINESE) {
            String[] text = category.split("-");

            if (text.length == format.length) {
                String resultText = new String();
                for (int i = 0, len = text.length; i < len; i++) {
                    resultText += (text[i] + format[i]);
                }
                return resultText;
            }
        }

        return category;
    }

    private static String decimalFormat(JSONObject setting) {
        boolean hasSeparator = setting.optBoolean("numSeparators", true);
        int type = setting.optInt("formatDecimal", BIReportConstant.TARGET_STYLE.FORMAT.NORMAL);//默认为自动
        return BIFormatHelper.getDecimalFormat(type, hasSeparator);
    }

    private static String scaleUnit(int level) {
        return BIFormatHelper.unitByLevel(level);
    }

    public static JSONObject createTextStyle(JSONObject settings, String text) {
            /*
    BI-8434 无数据时表格也需要有样式
    * */
        if (BIStringUtils.isEmptyString(text) || !StableUtils.isNumber(text)) {
            text = String.valueOf(Float.NEGATIVE_INFINITY);
        }
        try {
            Float num = Float.valueOf(text);
            int markResult = getTextCompareResult(settings, num);
            int iconStyle = settings.getInt("iconStyle");
            String textColor = getTextColor(settings, num);
            return JSONObject.create().put("markResult", markResult).put("iconStyle", iconStyle).put("color", textColor);
        } catch (Exception e) {
            return JSONObject.create();
        }
    }

    private static int getTextCompareResult(JSONObject settings, Float num) throws JSONException {
        if (!settings.has("mark") || num < settings.getInt("mark")) {
            return BIReportConstant.TARGET_COMPARE_RES.LESS;
        } else {
            return num == settings.getLong("mark") ? BIReportConstant.TARGET_COMPARE_RES.EQUAL : BIReportConstant.TARGET_COMPARE_RES.MORE;
        }
    }

    private static String getTextColor(JSONObject settings, Float num) throws JSONException {
        JSONArray conditions = settings.optJSONArray("conditions");
        if (null != conditions) {
            for (int i = 0; i < conditions.length(); i++) {
                JSONObject range = conditions.getJSONObject(i).getJSONObject("range");
                long min = range.optLong("min", Long.MIN_VALUE);
                long max = range.optLong("max", Long.MAX_VALUE);
                boolean minBoolean = range.optBoolean("closemin", false) ? num >= min : num > min;
                boolean maxBoolean = range.optBoolean("closemax", false) ? num <= max : num < max;
                if (minBoolean && maxBoolean) {
                    return conditions.getJSONObject(i).getString("color");
                }
            }
        }
        return StringUtils.EMPTY;
    }

}

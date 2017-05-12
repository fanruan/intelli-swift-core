package com.fr.bi.cal.analyze.report.report.widget.chart.export.utils;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.style.BITableItemStyle;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.style.ITableStyle;
import com.fr.bi.stable.constant.BIStyleConstant;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.awt.*;

/**
 * Created by Kary on 2017/2/26.
 * 样式计算
 */
public class SummaryTableStyleHelper {
    public static ITableStyle getHeaderStyles(String themeColor, int styleType) {
        switch(styleType) {
            case BIStyleConstant.TABLE_STYLE.STYLE_NORMAL:
                return new BITableItemStyle(themeColor, getContrastColor(themeColor), null);
            case BIStyleConstant.TABLE_STYLE.STYLE_GRADUAL:
                return new BITableItemStyle(themeColor, getContrastColor(themeColor), null);
            default:
                return new BITableItemStyle(null, null, null);
        }
    }

    public static ITableStyle getBodyStyles(String themeColor, int styleType) {
        return new BITableItemStyle(null, null, null);
    }

    public static ITableStyle getBodyStyles(String themeColor, int styleType, int index) throws JSONException {
        switch (styleType) {
            case BIStyleConstant.TABLE_STYLE.STYLE_NORMAL:
                String background = getRowColorByIndexAndThemeColor(index, themeColor);
                String filter = "progid:DXImageTransform.Microsoft.gradient(startColorstr=#0D" + themeColor.substring(1) + ",endColorstr=#0D" + themeColor.substring(1) + ");";
                return new BITableItemStyle(background, null, null, filter);
        }
        return new BITableItemStyle(null, null, null);
    }

    public static ITableStyle getLastSummaryStyles(String themeColor, int styleType) {
        switch (styleType) {
            case BIStyleConstant.TABLE_STYLE.STYLE_NORMAL:
                return new BITableItemStyle(themeColor, getContrastColor(themeColor), "bold");
            case BIStyleConstant.TABLE_STYLE.STYLE_GRADUAL:
                return new BITableItemStyle(themeColor, getContrastColor(themeColor), "bold");
            case BIStyleConstant.TABLE_STYLE.STYLE_SEPERATE:
                return new BITableItemStyle(null, themeColor, "bold");

        }
        return new BITableItemStyle(null, null, null);
    }

    private static String getRowColorByIndexAndThemeColor(int index, String color) {
        return index % 2 == 0 ? getOddColorByThemeColor(color) : getEvenColorByThemeColor(color);
    }

    //grb计算逻辑待补全
    private static String getEvenColorByThemeColor(String color) {
        return parseHEXAlpha2HEX(color, (float) 0.05);
    }

    private static String getOddColorByThemeColor(String color) {
        return parseHEXAlpha2HEX(color, (float) 0.2);
    }

    private static String parseHEXAlpha2HEX(String color, float a) {
        Color rgb = Color.decode(color);
        return "rgba(" + rgb.getRed() + "," + rgb.getGreen() + "," + rgb.getBlue() + "," + Float.toString(a) + ")";
    }

    private static String getContrastColor(String color) {
        if (StringUtils.isEmpty(color)) {
            return "";
        }
        if (isDarkColor(color)) {
            return "#ffffff";
        }
        return "#1a1a1a";
    }

    private static boolean isDarkColor(String color) {
        Color rgb = Color.decode(color);
        long grayLevel = Math.round(rgb.getRed() * 0.299 + rgb.getGreen() * 0.587 + rgb.getBlue() * 0.114);
        return grayLevel < 140;
    }
}

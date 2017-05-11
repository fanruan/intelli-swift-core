package com.fr.bi.cal.analyze.report.report.widget.chart.export.utils;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.style.BITableItemStyle;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.style.ITableStyle;
import com.fr.bi.stable.constant.BIStyleConstant;

/**
 * Created by Kary on 2017/2/26.
 * 样式计算
 */
public class SummaryTableStyleHelper {
    public static ITableStyle getHeaderStyles(String themeColor, int styleType) {
        return new BITableItemStyle(null, null, null);
    }

    public static ITableStyle getBodyStyles(String themeColor, int styleType) {
        return new BITableItemStyle(null, null, null);
    }

    public static ITableStyle getBodyStyles(String themeColor, int styleType, int index) {
        switch (styleType) {
            case BIStyleConstant.TABLE_STYLE.STYLE_NORMAL:
                String background = getRowColorByIndexAndThemeColor(index, themeColor);
                return new BITableItemStyle(background,null,null);
        }
        return new BITableItemStyle(null, null, null);
    }

    public static ITableStyle getLastSummaryStyles(String themeColor, String styleType) {
        return new BITableItemStyle(null, null, null);
    }

    private static String getRowColorByIndexAndThemeColor(int index, String color) {
       return index%2==0?getOddColorByThemeColor(color):getEvenColorByThemeColor(color);
    }
//grb计算逻辑待补全
    private static String getEvenColorByThemeColor(String color) {
        return "parseHEXAlpha2HEX(color, 0.2)";
    }

    private static String getOddColorByThemeColor(String color) {
        return "parseHEXAlpha2HEX(color, 0.05)";
    }
}

package com.fr.bi.conf.report.style;

import com.fr.base.CoreDecimalFormat;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class TargetStyleConstant {
    public final static Color[] CONDITION_COLOR = new Color[]{
            new Color(21, 75, 255),
            new Color(85, 85, 85),
            new Color(189, 107, 8),
            new Color(0, 174, 113),
            new Color(200, 45, 49),
            new Color(250, 0, 255)
    };
    private final static String DEFAULT_FORMAT = "#,###.##";
    private final static String[] DATA_FORMATS = new String[3];
    private final static String[] DATA_SIGN = new String[3];
    private final static String[] DATA_SHOWCLS_1 = new String[]{
            "fr_bi_data_show_icon_small_0", "fr_bi_data_show_icon_equal_0", "fr_bi_data_show_icon_large_0"
    };
    private final static String[] DATA_SHOWCLS_2 = new String[]{
            "fr_bi_data_show_icon_small_1", "fr_bi_data_show_icon_equal_1", "fr_bi_data_show_icon_large_1"
    };

    static {
        DATA_FORMATS[0] = "#,##0";
        DATA_FORMATS[1] = "#,##0.0";
        DATA_FORMATS[2] = "#,##0.00";
        DATA_SIGN[0] = "$";
        DATA_SIGN[1] = "¤";
        DATA_SIGN[2] = "%";
    }

    private static Map formatMap = new HashMap();

    public static DecimalFormat getFormat(String format) {
        DecimalFormat f = (DecimalFormat) formatMap.get(format);
        if (f == null) {
            f = new CoreDecimalFormat(new DecimalFormat(format), format);
            formatMap.put(format, f);
        }
        return f;
    }

    /**
     * @param formatIndex 数字样式
     * @param signIndex   符号
     * @return
     */
    public static String createFormatString(int formatIndex, int signIndex) {
        StringBuffer sb = new StringBuffer();
        if (signIndex == 0 || signIndex == 1) {
            sb.append(DATA_SIGN[signIndex]);
        }
        if (formatIndex == -1) {
            sb.append(DEFAULT_FORMAT);
        } else {
            sb.append(DATA_FORMATS[formatIndex]);
        }
        if (signIndex == 2) {
            sb.append(DATA_SIGN[signIndex]);
        }
        return sb.toString();
    }

    public static String creatHtmlCellContent(String format, double value, TargetSymbol symbol) {
        if (symbol.getSymbol_type() == 1) {
            return creatHtmlCellContent(format, value, DATA_SHOWCLS_1, symbol.getSymbol_value());
        } else if (symbol.getSymbol_type() == 2) {
            return creatHtmlCellContent(format, value, DATA_SHOWCLS_2, symbol.getSymbol_value());
        }
        return creatHtmlCellContent(format, value, DATA_SHOWCLS_1, symbol.getSymbol_value());
    }

    private static String creatHtmlCellContent(String format, double value, String[] cls, double line) {
        DecimalFormat f = getFormat(format);
        String v = f.format(new Double(value));
        StringBuffer sb = new StringBuffer();
        sb.append("<span style=\"vertical-align: top;\">").append(v).append("</span>");
        int index = 1;
        if (value > line) {
            index++;
        } else if (value < line) {
            index--;
        }
        sb.append("<span class=\"fr_bi_data_show " + cls[index] + "\"></span>");
        return sb.toString();
    }
}
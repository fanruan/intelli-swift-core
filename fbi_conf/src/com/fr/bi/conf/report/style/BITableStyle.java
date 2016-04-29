package com.fr.bi.conf.report.style;

import com.fr.base.CoreDecimalFormat;
import com.fr.base.Style;
import com.fr.base.background.ColorBackground;
import com.fr.base.core.StyleUtils;
import com.fr.general.FRFont;
import com.fr.report.cell.cellattr.CellGUIAttr;
import com.fr.stable.Constants;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * BI table控件的样式
 *
 * @author Daniel
 */
public class BITableStyle {
    private static BITableStyle style;
    private Style dimensionStyle = Style.getInstance();
    private Style dimentionOddStyle = dimensionStyle;
    private Style dimensionNumberStyle = dimensionStyle;
    private Style dimentionNumberOddStyle = dimensionStyle;
    private Style numberStyle = Style.getInstance();
    private Style noneValueStyle = numberStyle;
    private Style noneValueOddStyle = numberStyle;
    private Style numberOddStyle = numberStyle;
    private HashMap<Integer, Style> xTotal = new HashMap<Integer, Style>();
    private HashMap<Integer, Style> xNoneTotal = new HashMap<Integer, Style>();
    private HashMap<Integer, Style> yTotal = new HashMap<Integer, Style>();
    private HashMap<Integer, Style> yNoneTotal = new HashMap<Integer, Style>();
    private HashMap<Integer, Style> xStringTotal = new HashMap<Integer, Style>();
    private HashMap<Integer, Style> yStringTotal = new HashMap<Integer, Style>();
    //表头样式
    private HashMap<Integer, Style> titleStyle = new HashMap<Integer, Style>();
    //汇总
    private HashMap<Integer, Style> xTotalGreen = new HashMap<Integer, Style>();
    private HashMap<Integer, Style> xNoneTotalGreen = new HashMap<Integer, Style>();
    private HashMap<Integer, Style> yTotalGreen = new HashMap<Integer, Style>();
    private HashMap<Integer, Style> yNoneTotalGreen = new HashMap<Integer, Style>();
    private HashMap<Integer, Style> xStringTotalGreen = new HashMap<Integer, Style>();
    private HashMap<Integer, Style> yStringTotalGreen = new HashMap<Integer, Style>();
    private Style numberTotalStyle = dimensionStyle;
    private Style noneValueTotalStyle = dimensionStyle;
    private Style stringTotalStyle = dimensionStyle;

    public BITableStyle() {
        initStyle();
    }

    public static BITableStyle getInstance() {
        if (style == null) {
            style = new BITableStyle();
        }
        return style;
    }

    private void initStyle() {
        Color borderColor = new Color(0xB6D8B6);
        Color titleColor = new Color(0x3F3F3F);
        Color contentColor = new Color(0x3F3F3F);
        Color oddLine = new Color(0xf0f7f1);
        dimensionStyle = dimensionStyle.deriveBorder(Constants.LINE_THIN, borderColor, Constants.LINE_THIN, borderColor, Constants.LINE_THIN, borderColor, Constants.LINE_THIN, borderColor);
        dimensionStyle = StyleUtils.setReportFontForeground(dimensionStyle, titleColor);
        dimensionStyle = StyleUtils.setReportFontName(dimensionStyle, "MicroSoft Yahei");
        dimensionStyle = StyleUtils.setReportFontSize(dimensionStyle, 9);
        dimensionStyle = dimensionStyle.deriveHorizontalAlignment(Constants.CENTER);
        DecimalFormat f = new CoreDecimalFormat(new DecimalFormat("##.##"), "##.##");
        dimensionNumberStyle = dimensionStyle.deriveFormat(f);
        dimentionOddStyle = dimensionStyle.deriveBackground(ColorBackground.getInstance(oddLine));
        dimentionNumberOddStyle = dimensionNumberStyle.deriveBackground(ColorBackground.getInstance(oddLine));

        numberStyle = numberStyle.deriveBorder(Constants.LINE_THIN, borderColor, Constants.LINE_THIN, borderColor, Constants.LINE_THIN, borderColor, Constants.LINE_THIN, borderColor);
        numberStyle = StyleUtils.setReportFontForeground(numberStyle, contentColor);
        numberStyle = StyleUtils.setReportFontName(numberStyle, "Century Gothic");
        numberStyle = StyleUtils.setReportFontSize(numberStyle, 9);
        numberStyle = numberStyle.deriveHorizontalAlignment(Constants.CENTER);
        noneValueStyle = numberStyle.deriveHorizontalAlignment(Constants.CENTER);
        noneValueOddStyle = noneValueStyle.deriveBackground(ColorBackground.getInstance(oddLine));
        DecimalFormat decimalFormat = new CoreDecimalFormat(new DecimalFormat("#,###.##"), "#,###.##");
        numberStyle = numberStyle.deriveFormat(decimalFormat);
        numberOddStyle = numberStyle.deriveBackground(ColorBackground.getInstance(oddLine));

        numberTotalStyle = StyleUtils.boldReportFont(numberStyle);
        noneValueTotalStyle = StyleUtils.boldReportFont(noneValueStyle);

        initYsum(contentColor);
        initXsum();
        FRFont font = FRFont.getInstance("MicroSoft Yahei", 100, 10);
        font.setForeground(new Color(255, 255, 255));
        titleStyle.put(0, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(30, 140, 45))).deriveFRFont(font).deriveBorder(1, new Color(30, 140, 45), 1, new Color(30, 140, 45), 1, new Color(30, 140, 45), 1, new Color(30, 140, 45)));
        titleStyle.put(1, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(55, 155, 74))).deriveFRFont(font).deriveBorder(1, new Color(30, 140, 45), 1, new Color(30, 140, 45), 1, new Color(30, 140, 45), 1, new Color(30, 140, 45)));
        titleStyle.put(2, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(55, 155, 74))).deriveFRFont(font).deriveBorder(1, new Color(30, 140, 45), 1, new Color(30, 140, 45), 1, new Color(30, 140, 45), 1, new Color(30, 140, 45)));
        titleStyle.put(3, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(102, 184, 255))).deriveFRFont(font).deriveBorder(1, new Color(30, 140, 45), 1, new Color(30, 140, 45), 1, new Color(30, 140, 45), 1, new Color(30, 140, 45)));
        titleStyle.put(4, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(102, 184, 255))).deriveFRFont(font));
        titleStyle.put(5, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(102, 184, 255))).deriveFRFont(font));
        titleStyle.put(6, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(102, 184, 255))).deriveFRFont(font));
        titleStyle.put(7, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(102, 184, 255))).deriveFRFont(font));
        titleStyle.put(8, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(102, 184, 255))).deriveFRFont(font));

        xTotalGreen.put(0, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(221, 237, 221))));
        xNoneTotalGreen.put(0, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(221, 237, 221))));
        yTotalGreen.put(0, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(221, 237, 221))));
        yNoneTotalGreen.put(0, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(221, 237, 221))));
        xStringTotalGreen.put(0, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(221, 237, 221))));
        yStringTotalGreen.put(0, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(221, 237, 221))));
    }

    private void initXsum() {
        xTotal.put(0, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(204, 231, 255))));
        xNoneTotal.put(0, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(204, 231, 255))));
        xStringTotal.put(0, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(204, 231, 255))));

        xTotal.put(1, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(191, 225, 255))));
        xNoneTotal.put(1, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(191, 225, 255))));
        xStringTotal.put(1, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(191, 225, 255))));

        xTotal.put(2, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(178, 219, 255))));
        xNoneTotal.put(2, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(178, 219, 255))));
        xStringTotal.put(2, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(178, 219, 255))));

        xTotal.put(3, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(166, 213, 255))));
        xNoneTotal.put(3, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(166, 213, 255))));
        xStringTotal.put(3, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(166, 213, 255))));

        xTotal.put(4, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(153, 207, 255))));
        xNoneTotal.put(4, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(153, 207, 255))));
        xStringTotal.put(4, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(153, 207, 255))));

        xTotal.put(5, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(140, 201, 255))));
        xNoneTotal.put(5, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(140, 201, 255))));
        xStringTotal.put(5, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(140, 201, 255))));

        xTotal.put(6, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(128, 195, 255))));
        xNoneTotal.put(6, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(128, 195, 255))));
        xStringTotal.put(6, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(128, 195, 255))));

        xTotal.put(7, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(115, 190, 255))));
        xNoneTotal.put(7, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(115, 190, 255))));
        xStringTotal.put(7, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(115, 190, 255))));

        xTotal.put(8, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(102, 184, 255))));
        xNoneTotal.put(8, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(102, 184, 255))));
        xStringTotal.put(8, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(102, 184, 255))));
    }

    private void initYsum(Color contentColor) {
        yTotal.put(0, StyleUtils.setReportFontForeground(numberStyle, contentColor).deriveBackground(ColorBackground.getInstance(new Color(221, 237, 221))));
        yNoneTotal.put(0, StyleUtils.setReportFontForeground(noneValueStyle, contentColor).deriveBackground(ColorBackground.getInstance(new Color(221, 237, 221))));
        yStringTotal.put(0, StyleUtils.setReportFontForeground(dimensionStyle, contentColor).deriveBackground(ColorBackground.getInstance(new Color(221, 237, 221))));

        yTotal.put(1, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(166, 224, 174))));
        yNoneTotal.put(1, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(166, 224, 174))));
        yStringTotal.put(1, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(166, 224, 174))));

        yTotal.put(2, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(147, 219, 157))));
        yNoneTotal.put(2, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(147, 219, 157))));
        yStringTotal.put(2, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(147, 219, 157))));

        yTotal.put(3, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(129, 214, 140))));
        yNoneTotal.put(3, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(129, 214, 140))));
        yStringTotal.put(3, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(129, 214, 140))));

        yTotal.put(4, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(111, 209, 124))));
        yNoneTotal.put(4, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(111, 209, 124))));
        yStringTotal.put(4, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(111, 209, 124))));

        yTotal.put(5, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(94, 204, 109))));
        yNoneTotal.put(5, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(94, 204, 109))));
        yStringTotal.put(5, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(94, 204, 109))));

        yTotal.put(6, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(78, 199, 94))));
        yNoneTotal.put(6, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(78, 199, 94))));
        yStringTotal.put(6, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(78, 199, 94))));

        yTotal.put(7, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(62, 194, 80))));
        yNoneTotal.put(7, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(62, 194, 80))));
        yStringTotal.put(7, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(62, 194, 80))));

        yTotal.put(8, numberStyle.deriveBackground(ColorBackground.getInstance(new Color(47, 189, 66))));
        yNoneTotal.put(8, noneValueStyle.deriveBackground(ColorBackground.getInstance(new Color(47, 189, 66))));
        yStringTotal.put(8, dimensionStyle.deriveBackground(ColorBackground.getInstance(new Color(47, 189, 66))));
    }

    public Style getDimensionCellStyle(boolean isNumber, boolean isOdd) {
        return isNumber ? (isOdd ? dimentionNumberOddStyle : dimensionNumberStyle) : (isOdd ? dimentionOddStyle : dimensionStyle);
    }

    public Style getNumberCellStyle(Object cellValue, boolean isOdd) {
        return cellValue == null ? (isOdd ? noneValueOddStyle : noneValueStyle) : (isOdd ? numberOddStyle : numberStyle);
    }

    public Style getDimensionCellStyle(boolean isNumber, boolean isOdd, boolean isHyberLink) {
        Style style = isNumber ? (isOdd ? dimentionNumberOddStyle : dimensionNumberStyle) : (isOdd ? dimentionOddStyle : dimensionStyle);
        style = isHyberLink ? style.deriveFRFont(style.getFRFont().applyUnderline(Constants.LINE_THIN).applyForeground(Color.blue)) : style;
        return style;
    }

    public Style getTitleDimensionCellStyle(int level) {
        level = Math.min(level, 8);
        level = Math.max(0, level);
        return titleStyle.get(level);
    }

    public Style getNumberCellStyle(Object cellValue, boolean isOdd, boolean isHyberLink) {
        Style style = cellValue == null ? (isOdd ? noneValueOddStyle : noneValueStyle) : (isOdd ? numberOddStyle : numberStyle);
        style = isHyberLink ? style.deriveFRFont(style.getFRFont().applyUnderline(Constants.LINE_THIN).applyForeground(Color.blue)) : style;
        return style;
    }

    public Style getYTotalCellStyle(Object cellValue, int level) {
        level = Math.min(level, 8);
        level = Math.max(0, level);
        //added by young for 新城地产
        level = 0;
        return cellValue == null ? yNoneTotal.get(level) : yTotal.get(level);
    }

    public Style getXTotalCellStyle(Object cellValue, int level) {
        level = Math.min(level, 8);
        level = Math.max(0, level);
        //added by young for 新城地产
        level = 0;
        return cellValue == null ? yNoneTotal.get(level) : yTotal.get(level);
    }

    public Style getXSumStringCellStyle(int level) {
        level = Math.min(level, 8);
        level = Math.max(0, level);
        //added by young for 新城地产
        level = 0;
        return yStringTotal.get(level);
    }

    public Style getYSumStringCellStyle(int level) {
        level = Math.min(level, 8);
        level = Math.max(0, level);
        //added by young for 新城地产
        level = 0;
        return yStringTotal.get(level);
    }

    //这里有问题 先null
    public CellGUIAttr getCellAttr() {
        return new CellGUIAttr();
    }
}
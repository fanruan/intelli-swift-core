package com.fr.bi.conf.report.style;

import com.fr.base.CoreDecimalFormat;
import com.fr.base.Style;
import com.fr.base.background.ColorBackground;
import com.fr.base.core.StyleUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.FRFont;
import com.fr.general.GeneralUtils;
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
    private Style dimensionOddStyle = dimensionStyle;
    private Style dimensionNumberStyle = dimensionStyle;
    private Style dimensionNumberOddStyle = dimensionStyle;
    private Style numberStyle = Style.getInstance();
    private Style integerNumberStyle = numberStyle; //解决整数后面多个小数点问题
    private Style noneValueStyle = numberStyle;
    private Style noneValueOddStyle = numberStyle;
    private Style numberOddStyle = numberStyle;
    private Style integerNumberOddStyle = numberStyle;

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

    private Style totalStyle = Style.getInstance();
    private Style numberTotalStye = totalStyle;
    private Style integerTotalStyle = totalStyle;

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
        Color borderColor = new Color(0xeaeaea);
        Color titleColor = new Color(0x3F3F3F);
        Color contentColor = new Color(0x3F3F3F);
        Color oddLine = new Color(0xe0f1fa);
        Color evenLine = new Color(0xf7fbfd);
        dimensionStyle = dimensionStyle.deriveBorder(Constants.LINE_THIN, borderColor, Constants.LINE_THIN, borderColor, Constants.LINE_THIN, borderColor, Constants.LINE_THIN, borderColor);
        dimensionStyle = StyleUtils.setReportFontForeground(dimensionStyle, titleColor);
        dimensionStyle = StyleUtils.setReportFontName(dimensionStyle, "MicroSoft Yahei");
        dimensionStyle = StyleUtils.setReportFontSize(dimensionStyle, 9);
        dimensionStyle = dimensionStyle.deriveHorizontalAlignment(Constants.CENTER);
        dimensionStyle = dimensionStyle.deriveBackground(ColorBackground.getInstance(evenLine));
        DecimalFormat f = new CoreDecimalFormat(new DecimalFormat("##.##"), "##.##");
        dimensionNumberStyle = dimensionStyle.deriveFormat(f);
        dimensionOddStyle = dimensionStyle.deriveBackground(ColorBackground.getInstance(oddLine));
        dimensionNumberOddStyle = dimensionNumberStyle.deriveBackground(ColorBackground.getInstance(oddLine));

        numberStyle = numberStyle.deriveBorder(Constants.LINE_THIN, borderColor, Constants.LINE_THIN, borderColor, Constants.LINE_THIN, borderColor, Constants.LINE_THIN, borderColor);
        numberStyle = StyleUtils.setReportFontForeground(numberStyle, contentColor);
        numberStyle = StyleUtils.setReportFontName(numberStyle, "Century Gothic");
        numberStyle = StyleUtils.setReportFontSize(numberStyle, 9);
        numberStyle = numberStyle.deriveHorizontalAlignment(Constants.CENTER);
        numberStyle = numberStyle.deriveBackground(ColorBackground.getInstance(evenLine));
        noneValueStyle = numberStyle.deriveHorizontalAlignment(Constants.CENTER);
        noneValueOddStyle = noneValueStyle.deriveBackground(ColorBackground.getInstance(oddLine));
        totalStyle = noneValueStyle;
        DecimalFormat decimalFormat = new CoreDecimalFormat(new DecimalFormat("#,##0.00"), "#,##0.00");
        numberStyle = numberStyle.deriveFormat(decimalFormat);
        integerNumberStyle = numberStyle.deriveFormat(new CoreDecimalFormat(new DecimalFormat("#,###"), "#,###"));
        numberOddStyle = numberStyle.deriveBackground(ColorBackground.getInstance(oddLine));
        integerNumberOddStyle = numberOddStyle.deriveFormat(new CoreDecimalFormat(new DecimalFormat("#,###"), "#,###"));

        FRFont font = FRFont.getInstance("MicroSoft Yahei", 100, 10);
        font.setForeground(new Color(255, 255, 255));
        Color headerColor = new Color(101, 188, 231);
        Color headerBorderColor = new Color(234, 234, 234);
        titleStyle.put(0, dimensionStyle.deriveBackground(ColorBackground.getInstance(headerColor)).deriveFRFont(font).deriveBorder(1, headerBorderColor, 1, headerBorderColor, 1, headerBorderColor, 1, headerBorderColor));

        totalStyle = totalStyle.deriveBackground(ColorBackground.getInstance(new Color(101, 188, 231)));
        numberTotalStye = numberStyle.deriveBackground(ColorBackground.getInstance(new Color(101, 188, 231)));
        integerTotalStyle = integerNumberStyle.deriveBackground(ColorBackground.getInstance(new Color(101, 188, 231)));
    }

    public Style getDimensionCellStyle(boolean isNumber, boolean isOdd) {
        return isNumber ? (isOdd ? dimensionNumberOddStyle : dimensionNumberStyle) : (isOdd ? dimensionOddStyle : dimensionStyle);
    }

    public Style getNumberCellStyle(Object cellValue, boolean isOdd) {
        if (cellValue == null || ComparatorUtils.equals(cellValue, 0)) {
            return isOdd ? noneValueOddStyle : noneValueStyle;
        } else {
            String v = GeneralUtils.objectToString(cellValue);
            return isOdd ? (v.contains(".") ? numberOddStyle : integerNumberOddStyle) : (v.contains(".") ? numberStyle : integerNumberStyle);
        }
    }

    public Style getDimensionCellStyle(boolean isNumber, boolean isOdd, boolean isHyperLink) {
        Style style = isNumber ? (isOdd ? dimensionNumberOddStyle : dimensionNumberStyle) : (isOdd ? dimensionOddStyle : dimensionStyle);
        style = isHyperLink ? style.deriveFRFont(style.getFRFont().applyUnderline(Constants.LINE_THIN).applyForeground(Color.blue)) : style;
        return style;
    }

    public Style getTitleDimensionCellStyle(int level) {
        return titleStyle.get(0);
    }

    public Style getNumberCellStyle(Object cellValue, boolean isOdd, boolean isHyberLink) {
        if (cellValue == null || ComparatorUtils.equals(cellValue, 0)) {
            return isOdd ? noneValueOddStyle : noneValueStyle;
        }
        String v = GeneralUtils.objectToString(cellValue);
        Style style = v.contains(".") ? (isOdd ? numberOddStyle : numberStyle) : (isOdd ? integerNumberOddStyle : integerNumberStyle);
        style = isHyberLink ? style.deriveFRFont(style.getFRFont().applyUnderline(Constants.LINE_THIN).applyForeground(Color.blue)) : style;
        return style;
    }

    public Style getYTotalCellStyle(Object cellValue, int level) {
        if (cellValue != null && !ComparatorUtils.equals(cellValue, 0)) {
            String v = GeneralUtils.objectToString(cellValue);
            return v.contains(".") ? numberTotalStye : integerTotalStyle;
        }
        return totalStyle;
    }

    public Style getXTotalCellStyle(Object cellValue, int level) {
        if (cellValue != null && !ComparatorUtils.equals(cellValue, 0)) {
            String v = GeneralUtils.objectToString(cellValue);
            return v.contains(".") ? numberTotalStye : integerTotalStyle;
        }
        return totalStyle;
    }

    public Style getXSumStringCellStyle(int level) {
        return totalStyle;
    }

    public Style getYSumStringCellStyle(int level) {
        return totalStyle;
    }

    //这里有问题 先null
    public CellGUIAttr getCellAttr() {
        return new CellGUIAttr();
    }
}
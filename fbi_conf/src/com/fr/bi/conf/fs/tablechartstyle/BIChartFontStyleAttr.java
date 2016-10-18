package com.fr.bi.conf.fs.tablechartstyle;

/**
 * Created by windy on 2016/10/12.
 */
public class BIChartFontStyleAttr extends BIAbstractFontStyleAttr{

    public BIChartFontStyleAttr() {
    }

    public BIChartFontStyleAttr(String color, String fontStyle, String fontWidget) {
        super(color, fontStyle, fontWidget);
    }

    @Override
    public String getTag() {
        return "ChartFont";
    }

    @Override
    public String toString() {
        return "{" + "color:\"" + this.getColor() + "\", fontStyle: \"" + this.getFontStyle() +
                "\",fontWeight:\"" + this.getFontWidget() + "\"}";
    }
}

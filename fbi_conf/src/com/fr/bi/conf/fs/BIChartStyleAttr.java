package com.fr.bi.conf.fs;

import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by 小灰灰 on 2015/8/20.
 */
public class BIChartStyleAttr implements XMLable {
    public static final String XML_TAG = "ChartAttr";
    private int chartStyle = 0;
    private int defaultStyle = 0;
    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            String tagName = reader.getTagName();
            if (tagName.equals(XML_TAG)) {
                this.setChartStyle(reader.getAttrAsInt("chartStyle", 0));
                this.setDefaultStyle(reader.getAttrAsInt("defaultStyle", 0));
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("chartStyle", this.chartStyle);
        writer.attr("defaultStyle", this.defaultStyle);
        writer.end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getChartStyle() {
        return chartStyle;
    }

    public void setChartStyle(int chartStyle) {
        this.chartStyle = chartStyle;
    }

    public int getDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(int defaultStyle) {
        this.defaultStyle = defaultStyle;
    }
}
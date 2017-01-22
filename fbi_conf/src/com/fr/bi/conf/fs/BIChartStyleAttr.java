package com.fr.bi.conf.fs;

import com.fr.bi.conf.fs.tablechartstyle.*;
import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by 小灰灰 on 2015/8/20.
 */
public class BIChartStyleAttr implements XMLable {
    public static final String XML_TAG = "ChartAttr";
    private int chartStyle = 0;
    private int defaultStyle = 0;
    private BIChartFontStyleAttr chartFont = new BIChartFontStyleAttr();
    private BITitleFontStyleAttr titleFont = new BITitleFontStyleAttr();
    private BIMainBackgroundAttr mainBackground = new BIMainBackgroundAttr();
    private BITitleBackgroundAttr titleBackground = new BITitleBackgroundAttr();
    private BIWidgetBackgroundAttr widgetBackground = new BIWidgetBackgroundAttr();
    private String controlTheme = "";
    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            String tagName = reader.getTagName();
            if (tagName.equals(XML_TAG)) {
                this.setChartStyle(reader.getAttrAsInt("chartStyle", 0));
                this.setDefaultStyle(reader.getAttrAsInt("defaultStyle", 0));
                this.setControlTheme(reader.getAttrAsString("controlTheme", ""));
                reader.readXMLObject(new XMLReadable() {
                    @Override
                    public void readXML(XMLableReader xmLableReader) {
                        if(xmLableReader.isChildNode()){
                            if(ComparatorUtils.equals(xmLableReader.getTagName(), "ChartFont")){
                                chartFont.readXML(xmLableReader);
                            }
                            if(ComparatorUtils.equals(xmLableReader.getTagName(), "TitleFont")){
                                titleFont.readXML(xmLableReader);
                            }
                            if(ComparatorUtils.equals(xmLableReader.getTagName(), "MainBackground")){
                                mainBackground.readXML(xmLableReader);
                            }
                            if(ComparatorUtils.equals(xmLableReader.getTagName(), "TitleBackground")){
                                titleBackground.readXML(xmLableReader);
                            }
                            if(ComparatorUtils.equals(xmLableReader.getTagName(), "WidgetBackground")){
                                widgetBackground.readXML(xmLableReader);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("chartStyle", this.chartStyle);
        writer.attr("defaultStyle", this.defaultStyle);
        writer.attr("controlTheme", this.controlTheme);
        this.chartFont.writeXML(writer);
        this.titleFont.writeXML(writer);
        this.mainBackground.writeXML(writer);
        this.widgetBackground.writeXML(writer);
        this.titleBackground.writeXML(writer);
        writer.end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void setControlTheme(String controlTheme) {
        this.controlTheme = controlTheme;
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

    public String getControlTheme() {
        return controlTheme;
    }

    public BIWidgetBackgroundAttr getWidgetBackground() {
        return widgetBackground;
    }

    public BITitleBackgroundAttr getTitleBackground() {
        return titleBackground;
    }

    public BIMainBackgroundAttr getMainBackground() {
        return mainBackground;
    }

    public BITitleFontStyleAttr getTitleFont() {
        return titleFont;
    }

    public BIChartFontStyleAttr getChartFont() {
        return chartFont;
    }

    public void setChartFont(BIChartFontStyleAttr chartFont) {
        this.chartFont = chartFont;
    }

    public void setTitleFont(BITitleFontStyleAttr titleFont) {
        this.titleFont = titleFont;
    }

    public void setMainBackground(BIMainBackgroundAttr mainBackground) {
        this.mainBackground = mainBackground;
    }

    public void setTitleBackground(BITitleBackgroundAttr titleBackground) {
        this.titleBackground = titleBackground;
    }

    public void setWidgetBackground(BIWidgetBackgroundAttr widgetBackground) {
        this.widgetBackground = widgetBackground;
    }
}
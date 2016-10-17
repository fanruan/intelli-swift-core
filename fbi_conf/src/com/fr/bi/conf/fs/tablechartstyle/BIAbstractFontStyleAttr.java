package com.fr.bi.conf.fs.tablechartstyle;

import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by windy on 2016/10/12.
 */
public abstract class BIAbstractFontStyleAttr implements XMLable {
    private String color = "";
    private String fontStyle = "";
    private String fontWidget = "";

    public BIAbstractFontStyleAttr() {
    }

    public BIAbstractFontStyleAttr(String color, String fontStyle, String fontWidget) {
        this.color = color;
        this.fontStyle = fontStyle;
        this.fontWidget = fontWidget;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if(reader.isChildNode()){
            if(ComparatorUtils.equals(reader.getTagName(), getTag())){
                this.setColor(reader.getAttrAsString("color", ""));
                this.setFontStyle(reader.getAttrAsString("font-style", ""));
                this.setFontWidget(reader.getAttrAsString("font-weight", ""));
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(getTag());
        writer.attr("color", this.color);
        writer.attr("font-style", this.fontStyle);
        writer.attr("font-weight", this.fontWidget);
        writer.end();
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public void setFontWidget(String fontWidget) {
        this.fontWidget = fontWidget;
    }

    public String getColor() {
        return color;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public String getFontWidget() {
        return fontWidget;
    }

    public abstract String getTag();
}

package com.fr.bi.conf.fs.tablechartstyle;

import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by windy on 2016/10/12.
 */
public class BITitleFontStyleAttr extends BIAbstractFontStyleAttr {
    private String textAlign = "";

    public BITitleFontStyleAttr() {
    }

    public BITitleFontStyleAttr(String color, String fontStyle, String fontWidget, String textAlign) {
        super(color, fontStyle, fontWidget);
        this.textAlign = textAlign;
    }

    @Override
    public String getTag() {
        return "TitleFont";
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(getTag());
        writer.attr("color", this.getColor());
        writer.attr("fontStyle", this.getFontStyle());
        writer.attr("fontWeight", this.getFontWidget());
        writer.attr("textAlign", this.textAlign);
        writer.end();
    }

    @Override
    public void readXML(XMLableReader reader) {
        if(reader.isChildNode()){
            if(ComparatorUtils.equals(reader.getTagName(), getTag())){
                this.setColor(reader.getAttrAsString("color", ""));
                this.setFontStyle(reader.getAttrAsString("fontStyle", ""));
                this.setFontWidget(reader.getAttrAsString("fontWeight", ""));
                this.setTextAlign(reader.getAttrAsString("textAlign", "left"));
            }
        }
    }

    public void setTextAlign(String textAlign) {
        this.textAlign = textAlign;
    }

    public String getTextAlign() {
        return textAlign;
    }

    @Override
    public String toString() {
        return "{" + "color:\"" + this.getColor() + "\", fontStyle: \"" + this.getFontStyle() +
                "\",fontWeight:\"" + this.getFontWidget() + "\", textAlign:\"" + this.getTextAlign() + "\"}";
    }
}

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
        writer.attr("font-style", this.getFontStyle());
        writer.attr("font-weight", this.getFontWidget());
        writer.attr("text-Align", this.textAlign);
        writer.end();
    }

    @Override
    public void readXML(XMLableReader reader) {
        if(reader.isChildNode()){
            if(ComparatorUtils.equals(reader.getTagName(), getTag())){
                this.setColor(reader.getAttrAsString("color", ""));
                this.setFontStyle(reader.getAttrAsString("font-style", ""));
                this.setFontWidget(reader.getAttrAsString("font-weight", ""));
                this.setTextAlign(reader.getAttrAsString("text-align", "left"));
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
        return "{" + "color:\"" + this.getColor() + "\", font-style: \"" + this.getFontStyle() +
                "\",font-weight:\"" + this.getFontWidget() + "\", text-align:\"" + this.getTextAlign() + "\"}";
    }
}

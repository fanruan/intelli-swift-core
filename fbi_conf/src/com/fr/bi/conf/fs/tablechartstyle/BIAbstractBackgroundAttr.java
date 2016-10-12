package com.fr.bi.conf.fs.tablechartstyle;

import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by windy on 2016/10/12.
 */
public abstract class BIAbstractBackgroundAttr implements XMLable {
    private String value = "";
    private int type = 1;

    public BIAbstractBackgroundAttr() {
    }

    public BIAbstractBackgroundAttr(String value, int type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void readXML(XMLableReader reader) {
        if(reader.isChildNode()){
            if(ComparatorUtils.equals(reader.getTagName(), getTag())){
                this.setType(reader.getAttrAsInt("type", 1));
                this.setValue(reader.getAttrAsString("value", ""));
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(getTag());
        writer.attr("value", this.value);
        writer.attr("type", this.type);
        writer.end();
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(int type) {
        this.type = type;
    }

    public abstract String getTag();

    @Override
    public String toString() {
        return "{" +
                "value: '" + value + '\'' +
                ", type:" + type +
                '}';
    }
}

package com.fr.bi.base;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.provider.NameProvider;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by GUY on 2015/2/3.
 */
public class BIName implements XMLable, JSONTransform, NameProvider {
    @BICoreField
    protected String name = StringUtils.EMPTY;

    public BIName() {

    }

    public BIName(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return name;
    }

    public void setValue(String name) {
        this.name = name;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("name", this.name);
        return null;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        this.name = jo.optString("name", StringUtils.EMPTY);
    }

    @Override
    public void readXML(XMLableReader reader) {
        this.name = reader.getAttrAsString("name", StringUtils.EMPTY);
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.attr("name", this.name);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIName)) {
            return false;
        }

        BIName biName = (BIName) o;

        return !(name != null ? !ComparatorUtils.equals(name, biName.name) : biName.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BIName{");
        sb.append("name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
package com.fr.bi.base;

import com.fr.bi.base.provider.NameProvider;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by Young's on 2016/2/26.
 */
public class BIID implements XMLable, JSONTransform, NameProvider {

    protected String id = StringUtils.EMPTY;

    public BIID() {

    }

    public BIID(String id) {
        this.id = id;

    }

    public String getName() {
        return id;
    }

    public void setName(String id) {
        this.id = id;
    }

    @Override
    public String getValue() {
        return id;
    }

    public void setValue(String id) {
        this.id = id;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("did", this.id);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        this.id = jo.optString("did", StringUtils.EMPTY);
    }

    @Override
    public void readXML(XMLableReader reader) {
        this.id = reader.getAttrAsString("id", StringUtils.EMPTY);
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.attr("id", this.id);
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
        if (!(o instanceof BIID)) {
            return false;
        }

        BIID biName = (BIID) o;

        return !(id != null ? !ComparatorUtils.equals(id, biName.id) : biName.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BIID{");
        sb.append("id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

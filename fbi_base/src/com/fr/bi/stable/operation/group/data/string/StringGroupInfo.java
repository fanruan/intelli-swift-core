package com.fr.bi.stable.operation.group.data.string;

import com.fr.bi.base.BIName;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;

import java.io.Serializable;
import java.util.*;

/**
 * Created by GUY on 2015/1/29.
 */
public class StringGroupInfo extends BIName implements JSONParser, Cloneable, Serializable {

    public static final String XML_TAG = "StringGroupInfo";

    private static final long serialVersionUID = 403105911284163156L;


    @BICoreField
    private String[] groupValue = new String[0];

    /**
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("value")) {
            setValue(jo.getString("value"));
        }
        if (jo.has("content")) {
            JSONArray ja = jo.getJSONArray("content");
            int len = ja.length();
            groupValue = new String[len];
            for (int i = 0; i < len; i++) {
                JSONObject ob = ja.getJSONObject(i);
                groupValue[i] = ob.getString("value");
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        for (String s : groupValue) {
            writer.startTAG("value");
            writer.attr("v", s);
            writer.end();
        }
        writer.end();
    }

    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        final List<String> list = new ArrayList<String>();
        reader.readXMLObject(new XMLReadable() {
            @Override
            public void readXML(XMLableReader xmLableReader) {
                if (xmLableReader.isChildNode()) {
                    list.add(xmLableReader.getAttrAsString("v", StringUtils.EMPTY));
                }
            }
        });
        groupValue = list.toArray(new String[list.size()]);

    }

    /**
     * @return 分组集合
     */
    public Set createValueSet() {
        Set valueSet = new HashSet();
        int len = groupValue.length;
        for (int i = 0; i < len; i++) {
            valueSet.add(groupValue[i]);
        }

        return valueSet;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        JSONArray content = new JSONArray();
        for (int i = 0; i < groupValue.length; i++) {
            JSONObject value = new JSONObject();
            value.put("value",groupValue[i]);
            content.put(value);
        }
        jo.put("value", name);
        jo.put("content", content);
        return jo;
    }

    /**
     * @return code值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(groupValue);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StringGroupInfo other = (StringGroupInfo) obj;
        if (!super.equals(obj)) {
            return false;
        }
        if (!ComparatorUtils.equals(groupValue, other.groupValue)) {
            return false;
        }
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        StringGroupInfo gi = (StringGroupInfo) super.clone();
        gi.setValue(this.getValue());
        if (this.groupValue != null) {
            gi.groupValue = new String[this.groupValue.length];
            System.arraycopy(this.groupValue, 0, gi.groupValue, 0, this.groupValue.length);
        }
        return gi;
    }
}
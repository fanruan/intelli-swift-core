package com.fr.bi.conf.data.source.operator.add.selfrelation;

import com.fr.bi.base.BIUser;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by GUY on 2015/3/5.
 */
public class OneFieldIsometricUnionRelationOperator extends OneFieldUnionRelationOperator {
    public static final String XML_TAG = "OneFieldIsometricUnionRelationOprator";
    private static final long serialVersionUID = 7821174921688400659L;
    private char f = '0';

    public OneFieldIsometricUnionRelationOperator(int fieldLength) {
        this.fieldLength = fieldLength;
    }

    private String fill = new String(new char[]{f});
    @BICoreField
    private int fieldLength;

    public OneFieldIsometricUnionRelationOperator(long userId) {
        super(userId);
    }


    public OneFieldIsometricUnionRelationOperator() {
    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    /**
     * 将Java对象转换成JSON对象
     *
     * @return JSON对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("field_length", fieldLength);
        return jo;
    }

    @Override
    public String dealWithLayerValue(String v, int[] cz) {
        if (v != null) {
            for (int i = cz.length; i > -1; i--) {
                int len = i == 0 ? 0 : cz[i - 1];
                if (v.length() >= len) {
                    String temp = v.substring(len);
                    if (!isAllCharNone(temp)) {
                        return v.substring(0, i == cz.length ? fieldLength : cz[i]);
                    }
                }
            }
        }
        return v;
    }

    private boolean isAllCharNone(String v) {
        if (v != null && v.length() > 0) {
            for (int i = 0, ilen = v.length(); i < ilen; i++) {
                if (v.charAt(i) != f) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String dealWithValue(String value) {
        if (value == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer(value);
        for (int i = value.length(); i < fieldLength; i++) {
            sb.append(fill);
        }
        return sb.toString();
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jsonObject json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        super.parseJSON(jsonObject);
        fieldLength = jsonObject.getInt("field_length");
    }

    @Override
    public void readXML(XMLableReader reader) {
        String tagName = reader.getTagName();
        if (reader.isChildNode()) {

            this.user = new BIUser(reader.getAttrAsLong("userId", UserControl.getInstance().getSuperManagerID()));
            readFields(reader);
        } else {
            if (ComparatorUtils.equals(tagName, XML_TAG)) {
                this.idFieldName = reader.getAttrAsString("id_field_name", "");
                this.fieldLength = reader.getAttrAsInt("field_length", 0);
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("userId", user.getUserId());
        writer.attr("field_length", this.fieldLength)
                .attr("id_field_name", this.idFieldName);
        writeFields(writer);

        writer.end();
    }

}
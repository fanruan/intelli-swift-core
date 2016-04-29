package com.fr.bi.base;

import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import java.io.Serializable;

/**
 * 类型为字符类型的ID。当前BI中使用的多为类型为String的
 * <p/>
 * Created by Connery on 2015/12/15.
 */
public class BIBasicIdentity extends BIIdentity<String> implements Serializable, XMLable, JSONTransform {

    private static final long serialVersionUID = -397193333867039832L;

    public BIBasicIdentity(String id) {
        super(id);
    }

    /**
     * 将Java对象转换成JSON对象
     *
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("identity", identity.toString());
        return jo;
    }


    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("identity")) {
            identity = (jo.getString("identity"));
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {

        writer.startTAG(XML_TAG);
        writer.attr("identity", identity);
        writer.end();
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            this.identity = reader.getAttrAsString("identity", StringUtils.EMPTY);
        }
    }
}
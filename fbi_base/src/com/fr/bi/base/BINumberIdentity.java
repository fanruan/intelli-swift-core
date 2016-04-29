package com.fr.bi.base;

import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import java.io.Serializable;

/**
 * Created by Connery on 2015/12/23.
 */
public class BINumberIdentity extends BIIdentity<Long> implements Serializable, XMLable, Cloneable, JSONTransform {
    private static final long serialVersionUID = 6906984855858546046L;

    public BINumberIdentity(Long id) {
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
            identity = (jo.getLong("identity"));
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
            this.identity = reader.getAttrAsLong("identity", 0);
        }
    }
}
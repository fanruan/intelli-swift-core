package com.fr.bi.stable.data;

import com.fr.bi.base.BIBasicIdentity;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.json.JSONObject;

import java.io.Serializable;

/**
 * BITable的ID
 */
public class BITableID extends BIBasicIdentity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7840457178006364578L;


    public BITableID(String id) {
        super(id);
    }

    public BITableID(BITableID id) {
        super(id.getIdentityValue());
    }
    public BITableID() {
        super(BIStringUtils.emptyString());
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
}
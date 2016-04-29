package com.fr.bi.conf.data;


import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

/**
 * Created by 小灰灰 on 2015/12/25.
 */
public class BIBusiPackRole implements JSONTransform{
    private BIRoleAccessability[] authority_db;

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {

    }
}
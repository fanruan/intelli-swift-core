package com.fr.bi.conf.base.auth.data;

import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

/**
 * Created by kary on 16/5/9.
 */
public class BIPackageAuthority implements JSONTransform {
    private int roleId;
    private int roleType;
    private JSONObject filter;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getRoleType() {
        return roleType;
    }

    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }

    public JSONObject getFilter() {
        return filter;
    }

    public void setFilter(JSONObject filter) {
        this.filter = filter;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("role_id", roleId);
        jo.put("role_type", roleType);
        jo.put("filter", filter);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("role_id")) {
            this.roleId = jo.getInt("role_id");
        }
        if (jo.has("role_type")) {
            this.roleType = jo.getInt("role_type");
        }
        if (jo.has("filter")) {
            this.filter = jo.getJSONObject("filter");
        }
    }
}

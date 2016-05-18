package com.fr.bi.conf.base.pack.data;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

/**
 * Created by wuk on 16/5/9.
 */
public class BIPackAndAuthority implements JSONTransform {
    protected String biPackageID;
    protected String[] roleIdArray;


    public void setBiPackageID(String biPackageID) {
        this.biPackageID = biPackageID;
    }

    public String getBiPackageID() {
        return biPackageID;
    }

    public String[] getRoleIdArray() {
        return roleIdArray;
    }

    public void setRoleIdArray(String[] roleIdArray) {
        this.roleIdArray = roleIdArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BIPackAndAuthority that = (BIPackAndAuthority) o;

        return biPackageID != null ? biPackageID.equals(that.biPackageID) : that.biPackageID == null;

    }

    @Override
    public int hashCode() {
        return biPackageID != null ? biPackageID.hashCode() : 0;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        JSONArray rolesJA = new JSONArray();
        for(int i = 0; i < roleIdArray.length; i++) {
            rolesJA.put(roleIdArray[i]);
        }
        jo.put(biPackageID, rolesJA);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if(jo.has("roles")) {
            JSONArray ja = jo.getJSONArray("roles");
            roleIdArray = new String[ja.length()];
            for(int i = 0; i < ja.length(); i++) {
                JSONObject role = ja.getJSONObject(i);
                roleIdArray[i] = role.toString();
            }
        }
    }
}

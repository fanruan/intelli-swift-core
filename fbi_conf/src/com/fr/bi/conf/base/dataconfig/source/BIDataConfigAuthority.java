package com.fr.bi.conf.base.dataconfig.source;

import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

/**
 * BI数据配置的权限管理
 * Created by Young's on 2017/1/17.
 */
public class BIDataConfigAuthority implements JSONTransform {
    private String roleName;    //角色名称
    private int roleType;       //角色类型
    private String id;          //管理节点id
    private String parentId;    //管理节点parentId
    private boolean show;           //查看
    private boolean authorized;     //授权

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getRoleType() {
        return roleType;
    }

    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("roleName", roleName);
        jo.put("roleType", roleType);
        jo.put("id", id);
        jo.put("parentId", parentId);
        jo.put("show", show);
        jo.put("authorized", authorized);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("roleName")) {
            roleName = jo.getString("roleName");
        }
        if (jo.has("roleType")) {
            roleType = jo.getInt("roleType");
        }
        if (jo.has("id")) {
            id = jo.getString("id");
        }
        if (jo.has("parentId")) {
            parentId = jo.getString("parentId");
        }
        if (jo.has("show")) {
            show = jo.optBoolean("show", false);
        }
        if (jo.has("authorized")) {
            authorized = jo.optBoolean("authorized", false);
        }
    }
}

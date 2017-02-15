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
    private int view = 0;       //查看
    private int design = 0;     //授权

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

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getDesign() {
        return design;
    }

    public void setDesign(int design) {
        this.design = design;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("roleName", roleName);
        jo.put("roleType", roleType);
        jo.put("id", id);
        jo.put("parentId", parentId);
        jo.put("view", view);
        jo.put("design", design);
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
        if (jo.has("pId")) {
            parentId = jo.getString("pId");
        }
        if (jo.has("view")) {
            view = jo.optInt("view", 0);
        }
        if (jo.has("design")) {
            design = jo.optInt("design", 0);
        }
    }
}

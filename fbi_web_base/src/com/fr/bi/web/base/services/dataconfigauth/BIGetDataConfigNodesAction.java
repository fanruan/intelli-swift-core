package com.fr.bi.web.base.services.dataconfigauth;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.bi.conf.base.datasource.BIConnection;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.file.DatasourceManager;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Set;

/**
 * 获取可被授权的数据配置节点
 * Created by Young's on 2017/1/17.
 */
public class BIGetDataConfigNodesAction extends AbstractBIBaseAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        BIConnectionManager.getInstance().ensureInitTimeExist();
        //管理员可以授权所有数据配置相关节点
        if (ComparatorUtils.equals(UserControl.getInstance().getSuperManagerID(), userId)) {
            WebUtils.printAsJSON(res, createJSON4Admin());
        } else {
            WebUtils.printAsJSON(res, createJSONByUserId(userId));
        }
    }

    private JSONArray createJSON4Admin() throws Exception {
        JSONArray ja = new JSONArray();
        JSONObject connectionNodeJO = new JSONObject();
        connectionNodeJO.put("id", DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION);  //根节点
        ja.put(connectionNodeJO);       //数据连接节点

        JSONObject packageNodeJO = new JSONObject();
        packageNodeJO.put("id", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER);  //根节点
        ja.put(packageNodeJO);      //业务包管理

        JSONObject serverJO = new JSONObject();
        serverJO.put("pId", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER);
        serverJO.put("id", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER_CHILDREN.SERVER_CONNECTION);
        ja.put(serverJO);       //业务包管理->服务器数据集

        JSONObject packConnNodeJO = new JSONObject();
        packConnNodeJO.put("pId", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER);
        packConnNodeJO.put("id", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER_CHILDREN.DATA_CONNECTION);
        ja.put(packConnNodeJO);     //业务包管理->数据库

        createConnectionNodes4Admin(ja);

        JSONObject packGroupJO = new JSONObject();
        packGroupJO.put("pId", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER);
        packGroupJO.put("id", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER_CHILDREN.PACKAGE_GROUP);
        ja.put(packGroupJO);        //业务包管理->业务包分组

        JSONObject groupsJO = BICubeConfigureCenter.getPackageManager().createGroupJSON(UserControl.getInstance().getSuperManagerID());
        Iterator<String> ids = groupsJO.keys();
        while (ids.hasNext()) {
            String id = ids.next();
            JSONObject group = groupsJO.getJSONObject(id);
            String position = group.getString("init_time");
            JSONObject groupJO = new JSONObject();
            groupJO.put("pId", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER_CHILDREN.PACKAGE_GROUP);
            groupJO.put("id", position);
            groupJO.put("text", group.getString("name"));
            ja.put(groupJO);
        }

        createRootNodes4Admin(ja);
        return ja;
    }

    private void createRootNodes4Admin(JSONArray ja) throws Exception {
        //多路径设置
        JSONObject multiPathNodeJo = new JSONObject();
        multiPathNodeJo.put("id", DBConstant.DATA_CONFIG_AUTHORITY.MULTI_PATH_SETTING);
        ja.put(multiPathNodeJo);

        //业务包权限管理
        JSONObject packageAuthNodeJO = new JSONObject();
        packageAuthNodeJO.put("id", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_AUTHORITY);
        ja.put(packageAuthNodeJO);

        //FineIndex更新
        JSONObject fineIndexUpdateNodeJO = new JSONObject();
        fineIndexUpdateNodeJO.put("id", DBConstant.DATA_CONFIG_AUTHORITY.FINE_INDEX_UPDATE);
        ja.put(fineIndexUpdateNodeJO);
    }

    //管理员数据连接相关的节点
    private void createConnectionNodes4Admin(JSONArray ja) throws Exception {
        Iterator connectionNames = DatasourceManager.getInstance().getConnectionNameIterator();
        while (connectionNames.hasNext()) {
            JSONObject childConnNode = new JSONObject();
            JSONObject childPackNode = new JSONObject();
            String name = GeneralUtils.objectToString(connectionNames.next());
            String id = getConnectionIDByName(name);
            childConnNode.put("pId", DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION);
            childConnNode.put("id", DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION + id);
            childConnNode.put("text", name);
            ja.put(childConnNode);

            childPackNode.put("pId", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER_CHILDREN.DATA_CONNECTION);
            childPackNode.put("id", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER_CHILDREN.DATA_CONNECTION + id);
            childPackNode.put("text", name);
            ja.put(childPackNode);
        }
    }

    // 使用BIConnection对象中不变的属性initTime
    private String getConnectionIDByName(String name) {
        BIConnection connection = BIConnectionManager.getInstance().getBIConnection(name);
        return GeneralUtils.objectToString(connection.getInitTime());
    }

    //找到所有授权节点
    private JSONArray createJSONByUserId(long userId) throws Exception {
        JSONArray ja = new JSONArray();
        Set<BIDataConfigAuthority> authoritySet = BIConfigureManagerCenter.getDataConfigAuthorityManager().getDataConfigAuthoritiesByUserId(userId);

        for (BIDataConfigAuthority authority : authoritySet) {
            if (!ComparatorUtils.equals(authority.getDesign(), DBConstant.DATA_CONFIG_DESIGN.NO)) {
                String id = authority.getId();
                String pId = authority.getpId();
                JSONObject jo = new JSONObject();
                if (id != null) {
                    //删除的数据连接、分组 不显示
                    if (ComparatorUtils.equals(pId, DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER_CHILDREN.DATA_CONNECTION) ||
                            ComparatorUtils.equals(pId, DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION)) {
                        String connId = id.substring(pId.length());
                        String connName = getConnectionNameByID(connId);
                        if (connName == null) {
                            continue;
                        }
                        jo.put("text", connName);
                    }
                    if (ComparatorUtils.equals(pId, DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER_CHILDREN.PACKAGE_GROUP)) {
                        String groupId = id.substring(pId.length());
                        String groupName = getGroupNameByID(groupId);
                        if (groupName == null) {
                            continue;
                        }
                        jo.put("text", groupName);
                    }
                    jo.put("id", id);
                }
                if (pId != null) {
                    jo.put("pId", pId);
                }
                ja.put(jo);
            }
        }
        return ja;
    }

    private String getConnectionNameByID(String id) {
        Iterator<String> connNames = DatasourceManager.getInstance().getConnectionNameIterator();
        while (connNames.hasNext()) {
            String name = connNames.next();
            long initTime = BIConnectionManager.getInstance().getBIConnection(name).getInitTime();
            if (ComparatorUtils.equals(id, GeneralUtils.objectToString(initTime))) {
                return name;
            }
        }
        return null;
    }

    private String getGroupNameByID(String id) throws Exception {
        JSONObject groupsJO = BICubeConfigureCenter.getPackageManager().createGroupJSON(UserControl.getInstance().getSuperManagerID());
        Iterator<String> groupKeys = groupsJO.keys();
        while (groupKeys.hasNext()) {
            String gId = groupKeys.next();
            JSONObject group = groupsJO.getJSONObject(gId);
            if (ComparatorUtils.equals(id, group.getString("position"))) {
                return group.getString("name");
            }
        }
        return null;
    }

    @Override
    public String getCMD() {
        return "get_data_config_nodes";
    }
}

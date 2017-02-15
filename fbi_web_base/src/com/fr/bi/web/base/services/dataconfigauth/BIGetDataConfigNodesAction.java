package com.fr.bi.web.base.services.dataconfigauth;

import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 获取可被授权的数据配置节点
 * Created by Young's on 2017/1/17.
 */
public class BIGetDataConfigNodesAction extends AbstractBIBaseAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        //管理员可以授权所有数据配置相关节点
        if (ComparatorUtils.equals(UserControl.getInstance().getSuperManagerID(), userId)) {
            WebUtils.printAsJSON(res, createJSON4Admin());
        } else {
            WebUtils.printAsJSON(res, createJSONByUserId(userId));
        }
    }

    private JSONArray createJSON4Admin() throws Exception {
        JSONArray ja = new JSONArray();
        //数据连接节点
        JSONObject connectionNodeJO = new JSONObject();
        connectionNodeJO.put("id", DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION);  //根节点
        ja.put(connectionNodeJO);

        //业务包管理
        JSONObject packageNodeJO = new JSONObject();
        packageNodeJO.put("id", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER);  //根节点
        ja.put(packageNodeJO);

        Iterator connectionNames = DatasourceManager.getInstance().getConnectionNameIterator();
        while (connectionNames.hasNext()) {
            JSONObject childConnNode = new JSONObject();
            JSONObject childPackNode = new JSONObject();
            String name = GeneralUtils.objectToString(connectionNames.next());
            childConnNode.put("pId", DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION);
            childConnNode.put("id", DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION + name);  //使用name作为id
            childConnNode.put("text", name);
            ja.put(childConnNode);

            childPackNode.put("pId", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER);
            childPackNode.put("id", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER + name);  //使用name作为id
            childPackNode.put("text", name);
            ja.put(childPackNode);
        }

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

        ja.put(connectionNodeJO);
        return ja;
    }

    //找到所有授权节点
    public JSONArray createJSONByUserId(long userId) throws Exception {
        JSONArray ja = new JSONArray();
        Set<BIDataConfigAuthority> authoritySet = BIConfigureManagerCenter.getDataConfigAuthorityManager().getDataConfigAuthoritiesByUserId(userId);
        Iterator connNames = DatasourceManager.getInstance().getConnectionNameIterator();
        List<String> connNamesList = new ArrayList<String>();
        while (connNames.hasNext()) {
            connNamesList.add(GeneralUtils.objectToString(connNames.next()));
        }
        createAuthConnectionNodes(userId, DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION, ja);
        createAuthConnectionNodes(userId, DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER, ja);

        for (BIDataConfigAuthority authority : authoritySet) {
            if (ComparatorUtils.equals(authority.getDesign(), DBConstant.DATA_CONFIG_DESIGN.YES)) {
                String id = authority.getId();
                if (ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.MULTI_PATH_SETTING)) {
                    //多路径设置
                    JSONObject multiPathNodeJo = new JSONObject();
                    multiPathNodeJo.put("id", DBConstant.DATA_CONFIG_AUTHORITY.MULTI_PATH_SETTING);
                    ja.put(multiPathNodeJo);
                }
                if (ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_AUTHORITY)) {
                    //业务包权限管理
                    JSONObject packageAuthNodeJO = new JSONObject();
                    packageAuthNodeJO.put("id", DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_AUTHORITY);
                    ja.put(packageAuthNodeJO);
                }
                if (ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.FINE_INDEX_UPDATE)) {
                    //FineIndex更新
                    JSONObject fineIndexUpdateNodeJO = new JSONObject();
                    fineIndexUpdateNodeJO.put("id", DBConstant.DATA_CONFIG_AUTHORITY.FINE_INDEX_UPDATE);
                    ja.put(fineIndexUpdateNodeJO);
                }
            }
        }
        return ja;
    }

    private List<String> getAllConnectionNames() {
        Iterator connNames = DatasourceManager.getInstance().getConnectionNameIterator();
        List<String> connNamesList = new ArrayList<String>();
        while (connNames.hasNext()) {
            connNamesList.add(GeneralUtils.objectToString(connNames.next()));
        }
        return connNamesList;
    }

    private void createAuthConnectionNodes(long userId, String type, JSONArray ja) throws Exception {
        Set<BIDataConfigAuthority> authoritySet = BIConfigureManagerCenter.getDataConfigAuthorityManager().getDataConfigAuthoritiesByUserId(userId);
        List<String> connNamesList = getAllConnectionNames();
        for (BIDataConfigAuthority authority : authoritySet) {
            if (ComparatorUtils.equals(authority.getDesign(), DBConstant.DATA_CONFIG_DESIGN.YES)) {
                String id = authority.getId();
                String pId = authority.getParentId();
                JSONArray authConn = new JSONArray();
                if (ComparatorUtils.equals(pId, type)) {
                    String connName = id.substring(type.length());
                    if (connNamesList.contains(connName)) {
                        authConn.put(connName);
                        JSONObject childConnNode = new JSONObject();
                        childConnNode.put("pId", type);
                        childConnNode.put("id", type + connName);  //使用name作为id
                        childConnNode.put("text", connName);
                        ja.put(childConnNode);
                    }
                }
                if (authConn.length() > 0) {
                    JSONObject connectionNodeJO = new JSONObject();
                    connectionNodeJO.put("id", type);  //根节点
                    ja.put(connectionNodeJO);
                }
            }
        }
    }

    @Override
    public String getCMD() {
        return "get_data_config_nodes";
    }
}

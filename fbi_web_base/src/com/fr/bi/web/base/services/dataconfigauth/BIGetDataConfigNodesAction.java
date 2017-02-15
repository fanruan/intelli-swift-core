package com.fr.bi.web.base.services.dataconfigauth;

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

/**
 * 获取可被授权的数据配置节点
 * Created by Young's on 2017/1/17.
 */
public class BIGetDataConfigNodesAction extends AbstractBIBaseAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONArray ja = new JSONArray();
        //管理员可以授权所有数据配置相关节点
        if (ComparatorUtils.equals(UserControl.getInstance().getSuperManagerID(), userId)) {
            ja = createJSON4Admin();
        }

        WebUtils.printAsJSON(res, ja);

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

    @Override
    public String getCMD() {
        return "get_data_config_nodes";
    }
}

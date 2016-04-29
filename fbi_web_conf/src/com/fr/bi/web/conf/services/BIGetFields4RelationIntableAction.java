package com.fr.bi.web.conf.services;

import com.finebi.cube.api.BICubeManager;
import com.fr.bi.conf.base.pack.data.BIBusinessTable;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2014/5/20.
 * 获取配置信息中的表的字段
 */
public class BIGetFields4RelationIntableAction extends AbstractBIConfigureAction {
    @Override
    public String getCMD() {
        return "get_fields_4_relation_in_table";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        BIBusinessTable table = new BIBusinessTable(tableId, userId);
        JSONObject jo = table.createJSONWithFieldsInfo(BICubeManager.getInstance().fetchCubeLoader(userId));
        JSONObject tableFields = jo.getJSONObject("tableFields");
        tableFields.put("table_name_text", BIConfigureManagerCenter.getAliasManager().getAliasName(tableId, userId));
        WebUtils.printAsJSON(res, tableFields);
    }
}
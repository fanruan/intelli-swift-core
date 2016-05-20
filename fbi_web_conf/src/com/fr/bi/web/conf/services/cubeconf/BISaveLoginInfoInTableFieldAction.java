package com.fr.bi.web.conf.services.cubeconf;

import com.fr.bi.conf.base.cube.data.BILoginInfoInTableField;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/5/19.
 */
public class BISaveLoginInfoInTableFieldAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String table = WebUtils.getHTTPRequestParameter(req, "table_field");
        JSONObject tableJO = new JSONObject();
        if(table != null) {
            tableJO = new JSONObject(table);
        }
        BILoginInfoInTableField tableField = new BILoginInfoInTableField();
        tableField.parseJSON(tableJO);
        BIConfigureManagerCenter.getCubeConfManager().saveLoginInfoInTableField(tableField);
        try {
            BIConfigureManagerCenter.getCubeConfManager().persistData(userId);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public String getCMD() {
        return "save_login_info_in_table_field";
    }
}

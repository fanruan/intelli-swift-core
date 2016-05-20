package com.fr.bi.web.conf.services.cubeconf;

import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/5/19.
 */
public class BIGetLoginInfoInTableFieldAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONObject jo = BIConfigureManagerCenter.getCubeConfManager().getLoginInfoInTableField().createJSON();
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_login_info_in_table_field";
    }
}

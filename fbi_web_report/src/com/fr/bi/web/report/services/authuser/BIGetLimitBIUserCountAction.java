package com.fr.bi.web.report.services.authuser;

import com.fr.bi.conf.fs.FBIConfig;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/7/6.
 */
public class BIGetLimitBIUserCountAction extends ActionNoSessionCMD {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONObject jo = FBIConfig.getInstance().getUserAuthorAttr().createAuthLimitJo();
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_bi_limit_user";
    }
}

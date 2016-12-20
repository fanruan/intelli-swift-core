package com.fr.bi.web.report.services.authuser;

import com.fr.bi.web.base.utils.BIWebUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/10/21.
 */
public class BIGetUserEditAuthAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "get_user_edit_auth";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        WebUtils.printAsJSON(res, JSONObject.create().put("result", BIWebUtils.getUserEditViewAuth(userId)));
    }
}

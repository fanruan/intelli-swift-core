package com.fr.bi.web.report.services.authuser;

import com.fr.base.FRContext;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/7/6.
 */
public class BISetAuthUserAction extends ActionNoSessionCMD {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("success", saveUserAuth(req));
        WebUtils.printAsJSON(res, jo);
    }

    private boolean saveUserAuth(HttpServletRequest req) throws Exception {
        String userName = WebUtils.getHTTPRequestParameter(req, "username");
        String fullName = WebUtils.getHTTPRequestParameter(req, "fullname");
        int mode = WebUtils.getHTTPRequestIntParameter(req, "mode");
        Boolean success = false;
        if (StringUtils.isNotBlank(userName)) {
            Boolean remove = Boolean.parseBoolean(WebUtils.getHTTPRequestParameter(req, "remove"));
            if (remove) {
                //移除
                FBIConfig.getInstance().getUserAuthorAttr().removeUserByMode(userName, mode);
            } else {
                //增加、修改
                FBIConfig.getInstance().getUserAuthorAttr().addUserByMode(userName, fullName, mode);
            }
            FRContext.getCurrentEnv().writeResource(FBIConfig.getInstance());
            success = true;
        }
        return success;
    }

    @Override
    public String getCMD() {
        return "set_auth_user";
    }
}

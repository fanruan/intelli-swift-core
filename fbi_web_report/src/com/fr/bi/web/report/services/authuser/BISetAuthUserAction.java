package com.fr.bi.web.report.services.authuser;

import com.fr.base.FRContext;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by Young's on 2016/7/6.
 */
public class BISetAuthUserAction extends ActionNoSessionCMD{
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);

        PrintWriter writer = WebUtils.createPrintWriter(res);
        JSONObject jo = new JSONObject();
        jo.put("success", saveMobileUserAuth(req));
        WebUtils.printAsJSON(res, jo);
    }

    private boolean saveMobileUserAuth(HttpServletRequest req) throws Exception {
        String userName = WebUtils.getHTTPRequestParameter(req, "username");
        int mode = WebUtils.getHTTPRequestIntParameter(req, "mode");
        Boolean success = false;
        if (StringUtils.isNotBlank(userName)) {
            Boolean remove = Boolean.parseBoolean(
                    WebUtils.getHTTPRequestParameter(req, "remove"));
            JSONObject mobileUserAuth = FBIConfig.getInstance().getUserAuthorAttr().getBIAuthUserJoByMode(mode);
            long limitedCount = FBIConfig.getInstance().getUserAuthorAttr().getBIAuthUserLimitByMode(mode);

            if (remove) {
                //移除
                mobileUserAuth.remove(userName);
            } else if ( limitedCount > 0 && !mobileUserAuth.has(userName)
                    && mobileUserAuth.length() >= limitedCount ) {
                //限制新增达到上限
                return false;
            } else {
                //增加、修改
                String fullName = WebUtils.getHTTPRequestParameter(req, "fullname");
                mobileUserAuth.put(userName, fullName);
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

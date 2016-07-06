package com.fr.bi.web.report.services.authuser;

import com.fr.bi.conf.fs.FBIConfig;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by Young's on 2016/7/6.
 */
public class BIGetLimitBIUserCountAction extends ActionNoSessionCMD {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        PrintWriter pw = WebUtils.createPrintWriter(res);
        JSONObject jo = FBIConfig.getInstance().getUserAuthorAttr().createAuthLimitJo();
        pw.print(jo);
        pw.flush();
        pw.close();
    }

    @Override
    public String getCMD() {
        return "get_bi_limit_user";
    }
}

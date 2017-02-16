package com.fr.bi.web.report.services.authuser;

import com.fr.bi.web.base.utils.BIWebUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 新建分析 数据配置是否显示
 * Created by Young's on 2016/10/21.
 */
public class BIGetDesignConfigAuthAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "get_design_config_auth";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject jo = new JSONObject();
        jo.put("design", BIWebUtils.getUserEditViewAuth(userId));
        jo.put("config", BIWebUtils.showDataConfig(userId));
        WebUtils.printAsJSON(res, jo);
    }
}

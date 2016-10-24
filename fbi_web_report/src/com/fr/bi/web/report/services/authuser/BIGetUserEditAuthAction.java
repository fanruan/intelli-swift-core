package com.fr.bi.web.report.services.authuser;

import com.fr.bi.conf.fs.BIUserAuthorAttr;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
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
        JSONArray ja = FBIConfig.getInstance().getUserAuthorAttr().getUserAuthJaByMode(BIUserAuthorAttr.EDIT, StringUtils.EMPTY);
        JSONObject result = new JSONObject();
        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo = ja.getJSONObject(i);
            if (ComparatorUtils.equals(jo.getString("username"), UserControl.getInstance().getUser(userId).getUsername())) {
                result.put("result", true);
            }
        }
        WebUtils.printAsJSON(res, result);
    }
}

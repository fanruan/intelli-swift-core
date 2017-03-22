package com.fr.bi.web.report.services.authuser;

import com.fr.bi.conf.fs.BIUserAuthorAttr;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
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
        String users = WebUtils.getHTTPRequestParameter(req, "users");
        int mode = WebUtils.getHTTPRequestIntParameter(req, "mode");
        Boolean remove = WebUtils.getHTTPRequestBoolParameter(req, "remove");
        BIUserAuthorAttr userAuthorAttr = FBIConfig.getInstance().getUserAuthorAttr();
        JSONArray usersJA = new JSONArray(users);
        for (int i = 0; i < usersJA.length(); i++) {
            JSONObject userJO = usersJA.getJSONObject(i);
            String userName = userJO.getString("username");
            String realName = userJO.getString("realname");
            if (remove) {
                userAuthorAttr.removeUserByMode(userName, mode);
            } else {
                userAuthorAttr.addUserByMode(userName, realName, mode);
            }
        }
        FBIConfig.getProviderInstance().setUserAuthorAttr(userAuthorAttr);
        FBIConfig.getProviderInstance().writeResource();
    }

    @Override
    public String getCMD() {
        return "set_auth_user";
    }
}

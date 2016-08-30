package com.fr.bi.web.report.services.finecube;

import com.fr.bi.cal.analyze.session.BIWeblet;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.fs.privilege.auth.UserNamePassWordFSAuthenticationProvider;
import com.fr.json.JSONObject;
import com.fr.privilege.authentication.UsernamePasswordAuthentication;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/8/29.
 * fine cube 创建连接
 */
public class FCOpenSessionAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "fc_open_session";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        String username = WebUtils.getHTTPRequestParameter(req, "username");
        String password = WebUtils.getHTTPRequestParameter(req, "password");

        JSONObject result = new JSONObject();
        if (UserNamePassWordFSAuthenticationProvider.getInstance().authenticate(new UsernamePasswordAuthentication(username, password))) {
            User user = UserControl.getInstance().getByUserName(username);
            sessionID = SessionDealWith.generateSessionID(req, res, new BIWeblet(user.getId()));
            if (sessionID == null || !SessionDealWith.hasSessionID(sessionID)) {
                result.put("error", "generate session id failed!");
            } else {
                UserControl.getInstance().login(user.getId());
                result.put("sessionID", sessionID);
            }
        } else {
            result.put("error", "login failed!");
        }
        WebUtils.printAsJSON(res, result);
    }
}

package com.fr.bi.fe.service.noneedlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

/**
 * Created by young.
 */
public class FECheckUserNameAction extends ActionNoSessionCMD {
    @Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String username = WebUtils.getHTTPRequestParameter(req, "fsusername");
        JSONObject jo = new JSONObject();
        long userId = -1l;
        try {
        	userId = UserControl.getInstance().getUser(username);
		} catch (Exception e) {
			jo.put("result", false);
			WebUtils.printAsJSON(res, jo);
		}
        if(userId != -1l){
        	jo.put("result", true);
        	WebUtils.printAsJSON(res, jo);
        }
    }

    @Override
    public String getCMD() {
        return "check_user_name";
    }
}
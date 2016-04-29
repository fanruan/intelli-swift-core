package com.fr.bi.fe.service.noneedlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

/**
 * Created by young.
 */
public class FEGetAdminReplyAgreeByUserAction extends ActionNoSessionCMD {
    @Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
    	long userId = ServiceUtils.getCurrentUserID(req);
    	JSONObject jo = new JSONObject();
    	if(UserControl.getInstance().getUser(userId) == null){
    		jo.put("no_login", true);
    	}else{
    		String username = UserControl.getInstance().getUser(userId).getUsername();
    		String feedbackId = WebUtils.getHTTPRequestParameter(req, "feedback_id");
    		boolean isExsit = FineExcelUserService.getInstance().getAdminReplyAgreeByUser(Integer.parseInt(feedbackId), username);
    		jo.put("result", isExsit);
    	}
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_admin_reply_agree_by_user";
    }
}
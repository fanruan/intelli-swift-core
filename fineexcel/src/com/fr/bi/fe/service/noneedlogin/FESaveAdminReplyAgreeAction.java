package com.fr.bi.fe.service.noneedlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

/**
 * Created by young.
 */
public class FESaveAdminReplyAgreeAction extends ActionNoSessionCMD {
    @Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
    	long userId = ServiceUtils.getCurrentUserID(req);
        String username = UserControl.getInstance().getUser(userId).getUsername();
    	String feedbackId = WebUtils.getHTTPRequestParameter(req, "feedback_id");
        String adminAgree = WebUtils.getHTTPRequestParameter(req, "admin_agree");
        int status = 1;
        FineExcelUserService.getInstance().saveAdminReplyAgree(Integer.parseInt(feedbackId), username, Integer.parseInt(adminAgree), status);
    }

    @Override
    public String getCMD() {
        return "save_admin_reply_agree";
    }
}
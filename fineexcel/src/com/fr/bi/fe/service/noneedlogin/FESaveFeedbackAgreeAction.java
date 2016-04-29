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
public class FESaveFeedbackAgreeAction extends ActionNoSessionCMD {
    @Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String username = UserControl.getInstance().getUser(userId).getUsername();
        String id = WebUtils.getHTTPRequestParameter(req, "id");
        String agreeCount = WebUtils.getHTTPRequestParameter(req, "agreeCount");
        String status = WebUtils.getHTTPRequestParameter(req, "status");
        FineExcelUserService.getInstance().saveFeedbackAgree(Integer.parseInt(id), Integer.parseInt(agreeCount));
        FineExcelUserService.getInstance().saveFeedbackAgreeByUser(Integer.parseInt(id), username, Integer.parseInt(status));
        
    }

    @Override
    public String getCMD() {
        return "save_feedback_agree";
    }
}
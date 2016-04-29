package com.fr.bi.fe.service.noneedlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

/**
 * Created by young.
 */
public class FEGetAllFeedbackAction extends ActionNoSessionCMD {
    @Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject jo = new JSONObject();
        JSONArray ja_ = new JSONArray();
        if(UserControl.getInstance().getUser(userId) == null){
        	ja_ = null;
        	jo.put("no_login", true);
        }else{
        	ja_ = FineExcelUserService.getInstance().getFeedbackAdd1ByUser(UserControl.getInstance().getUser(userId).getUsername());
        	jo.put("feedbackAdd1", ja_);
        }
        String page = WebUtils.getHTTPRequestParameter(req, "page");
        JSONArray ja = FineExcelUserService.getInstance().getFeedbackByPage(Integer.parseInt(page));
        jo.put("feedbackAll", ja);
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_all_feedback";
    }
}
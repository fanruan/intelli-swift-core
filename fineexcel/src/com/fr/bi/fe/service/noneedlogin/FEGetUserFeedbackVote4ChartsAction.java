package com.fr.bi.fe.service.noneedlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

/**
 * Created by young.
 */
public class FEGetUserFeedbackVote4ChartsAction extends ActionNoSessionCMD {
    @Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
    	JSONObject jo = new JSONObject();
    	jo = FineExcelUserService.getInstance().getUserVote();
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_user_feedback_vote_4_charts";
    }
}
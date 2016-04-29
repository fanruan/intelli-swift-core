package com.fr.bi.fe.service.noneedlogin;

import com.fr.bi.fe.fs.data.FineExcelUserFeedback;
import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.bi.web.services.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by young.
 */
public class FESaveUserFeedbackAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String vote = WebUtils.getHTTPRequestParameter(req, "vote");
        vote = vote.substring(1, vote.length() - 1);
        String []votes = vote.split(",");
        int [] feedbackVote = new int[5];
        for(int i = 0; i < votes.length; i++){
        	if(votes[i].equals("true")){
        		feedbackVote[i] = 1;
        	}else{
        		feedbackVote[i] = 0;
        	}
        }
        String description = WebUtils.getHTTPRequestParameter(req, "description");
        String picture = WebUtils.getHTTPRequestParameter(req, "picture");
        String fullFileName = WebUtils.getHTTPRequestParameter(req, "full_file_name");
        
        FineExcelUserFeedback feedback = new FineExcelUserFeedback();
        feedback.setUserId(userId);
        feedback.setVote(feedbackVote);
        feedback.setDescription(description);
        feedback.setFullFileName(fullFileName);
        feedback.setPicture(picture);
        
        FineExcelUserService fineExcelUserService = new FineExcelUserService();
        boolean result = fineExcelUserService.saveUserFeedback(feedback);
        System.out.println();
        JSONObject jo = new JSONObject();
        jo.put("result", result);
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "save_user_feedback";
    }
}
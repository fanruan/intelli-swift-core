package com.fr.bi.fe.service.noneedlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.bi.fe.fs.data.FineExcelUserModel;
import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

/**
 * Created by young.
 */
public class FECheckUserEmailAction extends ActionNoSessionCMD {
    @Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String username = WebUtils.getHTTPRequestParameter(req, "username");
        FineExcelUserService fineExcelUserService = new FineExcelUserService();
        FineExcelUserModel user = fineExcelUserService.getFEUserByEmail(username);
        JSONObject jo = new JSONObject();
        if(user != null){
        	jo.put("result", false);
		}else{			
        	jo.put("result", true);
        }
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "check_user_email";
    }
}
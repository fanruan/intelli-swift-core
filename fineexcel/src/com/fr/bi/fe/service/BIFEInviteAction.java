package com.fr.bi.fe.service;

import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2015/1/20.
 */
public class BIFEInviteAction extends ActionNoSessionCMD{

    private static final String HOSTURL = "http://hihidata.com";
    @Override
    public String getCMD() {
        return "invite_regist";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String mail = WebUtils.getHTTPRequestParameter(req, "mail");
        JSONArray mails = new JSONArray(mail);
        JSONArray username = new JSONArray();
        JSONArray link = new JSONArray();
        for(int i = 0; i < mails.length(); i ++){
            username.put(mails.getString(i));
            link.put(HOSTURL);
        }
        JSONObject sub = new JSONObject();
        sub.put("%username%", username);
        sub.put("%hihidatalink%", link);
        String subject = "service";
        String from = "service@hihidata.com";
        FineExcelUserService.sendEmailBySendCould(mails, sub, subject, from, 0);
    }
}
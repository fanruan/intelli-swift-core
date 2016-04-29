package com.fr.bi.fe.service.noneedlogin;

import com.fr.base.ConfigManager;
import com.fr.bi.fe.fs.data.FineExcelUserModel;
import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.FSLoadLoginAction;
import com.fr.general.GeneralContext;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
/**
 * Created by young on 2014/12/17.
 */
public class BIFERegisterAction extends FSLoadLoginAction {

    private static final String HOSTURL = "http://hihidata.com";
    @Override
    public String getCMD() {
        return "register";
    }

    @Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String email = WebUtils.getHTTPRequestParameter(req, "username");
        String password = WebUtils.getHTTPRequestParameter(req, "password");
        String phone = WebUtils.getHTTPRequestParameter(req, "phone");
        JSONObject jo = new JSONObject();
        jo.put("username", email);
        jo.put("realname", email.split("@")[0]);
        jo.put("password", password);
        jo.put("email", email);
        jo.put("status", 0);
        jo.put("mobile", phone);
        jo.put("validateCode", FineExcelUserService.encode2hex(email));
        FineExcelUserModel feUser = new FineExcelUserModel();
        feUser.parseJSON(jo);
        feUser.setEmail(email);
        feUser.setRegisterDate(new Date());
        feUser.setUsername(email.split("@")[0]);
        User user = new User();
        user.parseJSON(jo);
        JSONObject result = new JSONObject();
        boolean notExsit = UserControl.getInstance().addUser(user);
        FineExcelUserModel tempUser = FineExcelUserService.getInstance().getFEUserByEmail(email);
        feUser.setId(user.getId());
        String message = "success";
        if(notExsit && tempUser == null){
            //邮件的内容
            JSONArray je = new JSONArray();
            je.put(email);

            JSONArray username = new JSONArray();
            username.put(email);
            JSONArray link = new JSONArray();
            link.put(HOSTURL + "/?op=fbi_no_need_login&cmd=email_validate&email=" + email + "&validateCode="+feUser.getValidateCode());
            JSONObject sub = new JSONObject();
            sub.put("%username%", username);
            sub.put("%hihidatalink%", link);
            String subject = "Hihidata注册邮件";
            String from = "service@hihidata.com";

            //发送邮件
        	if(FineExcelUserService.sendEmailBySendCould(je, sub, subject, from, 1)){
                FineExcelUserService.getInstance().saveFineExcelUser2DB(feUser);
        	}else{
        		message = "fail";
        		UserControl.getInstance().deleteUser(UserControl.getInstance().getUser(email));
        	}
            result.put("result", message);
        }else{
            message = "exsit";
            UserControl.getInstance().deleteUser(UserControl.getInstance().getUser(email));
            result.put("result", message);
        }
        WebUtils.printAsJSON(res, result);
    }
}
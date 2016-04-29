package com.fr.bi.fe.service.noneedlogin;

import com.fr.base.ConfigManager;
import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.fs.control.UserControl;
import com.fr.general.GeneralContext;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by young.
 */
public class FEResetPasswordAction extends ActionNoSessionCMD {

    @Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String email = WebUtils.getHTTPRequestParameter(req, "email");
        
        long userId = UserControl.getInstance().getUser(email);
        
        FineExcelUserService fineExcelUserService = new FineExcelUserService();
        JSONObject jo = new JSONObject();
        if( userId == -99 ){
        	jo.put("result", false);
        }else{
        	String validateCode = fineExcelUserService.getValidateCodeByUserId(userId);
        	if(validateCode.equals("")){
        		jo.put("result", false);
        	}else{
        		StringBuffer sb=new StringBuffer("点击下面链接重置密码为123456！<br>");
            	sb.append( FineExcelUtils.getRemotHost() +  GeneralContext.getCurrentAppNameOfEnv() + "/" + ConfigManager.getInstance().getServletMapping()
            			+ "?op=fbi_no_need_login&cmd=reset_password_validate" ); 
            	sb.append("&email=" + email);
            	sb.append("&validateCode=");
            	sb.append(validateCode);
            	sb.append("<br>");
            	sb.append("重置后，请尽快登录并修改密码！");
            	//发送邮件
            	FineExcelUserService.send(email, sb.toString(), "reset");
            	
        		jo.put("result", true);
        	}
        }
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "reset_password";
    }
}
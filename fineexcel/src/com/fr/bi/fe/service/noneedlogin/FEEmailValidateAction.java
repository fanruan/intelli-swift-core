package com.fr.bi.fe.service.noneedlogin;

import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FEEmailValidateAction extends ActionNoSessionCMD {

	@Override
	public String getCMD() {
		return "email_validate";
	}

	@Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String email = WebUtils.getHTTPRequestParameter(req, "email");//获取email
        String validateCode = WebUtils.getHTTPRequestParameter(req, "validateCode");//激活码
        
        Map map = new HashMap();
        Locale locale = req.getLocale();

        map.put("company", FBIConfig.getInstance().getLoginTitle4FS());
        map.put("backgroundColor", FBIConfig.getInstance().getBackgroundColor());
        map.put("backgroundImageID", FBIConfig.getInstance().getBgImageID4FS());
        map.put("dark", FBIConfig.getInstance().isDark() ? "yes" : "no");

        try {
        	String message = FineExcelUserService.getInstance().processActivate(email , validateCode);//调用激活方法
        	map.put("validateMes",message);
        	WebUtils.writeOutTemplate("/com/fr/bi/fe/web/html/login_fineexcel.html", res, map);
        } catch (Exception e) {
        	map.put("validateMes","邮箱激活出现异常");
        	WebUtils.writeOutTemplate("/com/fr/bi/fe/web/html/login_fineexcel.html", res, map);
        }
	}

}
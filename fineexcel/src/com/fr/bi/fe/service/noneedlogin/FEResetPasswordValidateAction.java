package com.fr.bi.fe.service.noneedlogin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

/**
 * Created by young.
 */
public class FEResetPasswordValidateAction extends ActionNoSessionCMD {
    @Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String email = WebUtils.getHTTPRequestParameter(req, "email");
        String validateCode = WebUtils.getHTTPRequestParameter(req, "validateCode");
        
        FineExcelUserService fineExcelUserService = new FineExcelUserService();
        long userId = UserControl.getInstance().getUser(email);
        User user = UserControl.getInstance().getUser(userId);
        String validateCode_ = fineExcelUserService.getValidateCodeByUserId(userId);
        if(validateCode.equals(validateCode_)){
        	user.setPassword("123456");
        	UserControl.getInstance().updateUserAuthInfo(user);
        }else{
        	
        }
        Map map = new HashMap();
        Locale locale = req.getLocale();

        map.put("company",FBIConfig.getInstance().getLoginTitle4FS());
        map.put("backgroundColor", FBIConfig.getInstance().getBackgroundColor());
        map.put("backgroundImageID",FBIConfig.getInstance().getBgImageID4FS());
        map.put("dark", FBIConfig.getInstance().isDark() ? "yes" : "no");
        WebUtils.writeOutTemplate("/com/fr/bi/fe/web/html/login_fineexcel.html", res, map);
    }

    @Override
    public String getCMD() {
        return "reset_password_validate";
    }
}
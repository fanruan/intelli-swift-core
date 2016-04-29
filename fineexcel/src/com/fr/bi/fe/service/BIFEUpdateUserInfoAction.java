package com.fr.bi.fe.service;

import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young on 2015/1/12.
 */
public class BIFEUpdateUserInfoAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "change_user_info";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String item = WebUtils.getHTTPRequestParameter(req, "item");
        String value = WebUtils.getHTTPRequestParameter(req, "value");

        User user = UserControl.getInstance().getUser(userId);
        if(item.equals("username")){
            user.setRealname(value);
        }else{
            user.setMobile(value);
        }
        UserControl.getInstance().updateUserAuthInfo(user);

    }
}
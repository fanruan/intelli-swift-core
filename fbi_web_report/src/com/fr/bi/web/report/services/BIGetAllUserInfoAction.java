package com.fr.bi.web.report.services;

import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Baron on 2015/11/20.
 */
public class BIGetAllUserInfoAction extends ActionNoSessionCMD {
    /**
     * 返回字符串
     *
     * @return
     */
    @Override
    public String getCMD() {
        return "get_all_user_info";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        List<User> userList = UserControl.getInstance().findAllUser();
        JSONArray ja = new JSONArray();
        for(int i = 0; i < userList.size(); i++){
            User user = userList.get(i);
            if(user.getId() != userId) {
                JSONObject jo = user.createJSON4Share();
                jo.put("roles", UserControl.getInstance().getAllSRoleNames(user.getId()));
                ja.put(jo);
            }
        }
        WebUtils.printAsJSON(res, ja);
    }
}
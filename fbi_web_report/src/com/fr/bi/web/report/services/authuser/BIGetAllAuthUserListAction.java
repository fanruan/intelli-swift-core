package com.fr.bi.web.report.services.authuser;

import com.fr.bi.conf.fs.FBIConfig;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Young's on 2016/7/6.
 */
public class BIGetAllAuthUserListAction extends ActionNoSessionCMD {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        PrintWriter writer = WebUtils.createPrintWriter(res);
        long userid = ServiceUtils.getCurrentUserID(req);
        String keyword = WebUtils.getHTTPRequestParameter(req, "keyword");
        if (keyword == null) {
            String filter = WebUtils.getHTTPRequestParameter(req, "filter");
            keyword = filter == null ? "" : filter;
        }
        int mode = WebUtils.getHTTPRequestIntParameter(req, "mode");
        List userlist = UserControl.getInstance().findAllAuthUser(userid);
        JSONArray allUsers = new JSONArray();
        for (int i = 0, len = userlist.size(); i < len; i++) {
            User user = (User) userlist.get(i);
            if (isKeywordInUserInfo(user.createUnEditInfoJSONConfig(), keyword)) {
                if (!isUserAllowedBILogin(user.getUsername(), mode)) {
                    allUsers.put(user.createEditInfoJSONConfig());
                }
            }
        }
        writer.print(allUsers.toString());
        writer.flush();
        writer.close();
    }

    /**
     * 关键字是否在用户信息中
     *
     * @param jo      用户信息
     * @param keyword 关键字
     * @return 返回是否存在
     */
    private boolean isKeywordInUserInfo(JSONObject jo, String keyword) {
        if (containsNoCase(jo.optString("username", ""), keyword)) {
            return true;
        }
        if (containsNoCase(jo.optString("realname", ""), keyword)) {
            return true;
        }
        return false;
    }

    private boolean containsNoCase(String str1, String str2) {
        return str1.toUpperCase().contains(str2.toUpperCase());
    }

    /**
     * 是否已经在允许的名字里面
     *
     * @param username 用户
     */
    private boolean isUserAllowedBILogin(String username, int mode) {
        if (FBIConfig.getInstance().getUserAuthorAttr().getBIAuthUserLimitByMode(mode) == 0) {
            return true;
        }
        if (FBIConfig.getInstance().getUserAuthorAttr().getBIAuthUserJoByMode(mode).has(username)) {
            return true;
        }
        return false;
    }

    @Override
    public String getCMD() {
        return "get_all_auth_user_list";
    }
}

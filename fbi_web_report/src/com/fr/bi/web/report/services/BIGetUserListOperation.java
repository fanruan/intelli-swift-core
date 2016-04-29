package com.fr.bi.web.report.services;

import com.fr.bi.web.base.operation.BIUserOperationManager;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Connery on 2014/12/15.
 */
public class BIGetUserListOperation extends ActionNoSessionCMD {
    /**
     * 返回 字符串
     *
     * @return
     */
    @Override
    public String getCMD() {
        return "get_user_list";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        JSONObject json = BIUserOperationManager.getUserNameJson();
        Map map = new HashMap<String, String>();
        map.put("userList", json);
        WebUtils.writeOutTemplate("/com/fr/bi/web/html/bi_user_list.html", res, map);
    }
}
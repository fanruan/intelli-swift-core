package com.fr.bi.web.service.action;

import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/6/2.
 */
public class BIAnalysisETLGetGeneratingStatusAction extends AbstractAnalysisETLAction{

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        JSONObject json = new JSONObject();
        WebUtils.printAsJSON(res, json);
    }


    @Override
    public String getCMD() {
        return "get_cube_status";
    }
}


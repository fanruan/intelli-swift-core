package com.fr.bi.web.base.services;

import com.fr.bi.stable.utils.program.BIPhoneticismUtils;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.json.JSONArray;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetPyAction extends AbstractBIBaseAction {

    /**
     * @return
     */
    @Override
    public String getCMD() {
        return "get_py";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {

        JSONArray result = new JSONArray();
        String name = WebUtils.getHTTPRequestParameter(req, "name");
        result.put(BIPhoneticismUtils.getPingYin(name));

        WebUtils.printAsJSON(res, result);
    }
}
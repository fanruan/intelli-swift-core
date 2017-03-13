package com.fr.bi.services;

import com.fr.bi.resource.ResourceHelper;
import com.fr.web.core.ActionNoSessionCMD;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by windy on 2017/1/13.
 */
public class BIGetH5Action extends ActionNoSessionCMD {

    public static final String CMD = "h5.js";

    @Override
    public String getCMD() {
        return CMD;
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ResourceHelper.getH5Js(req, res);
    }
}

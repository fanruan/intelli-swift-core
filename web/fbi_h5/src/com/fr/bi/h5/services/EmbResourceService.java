package com.fr.bi.h5.services;

import com.fr.bi.h5.resource.ResourceHelper;
import com.fr.web.core.ActionNoSessionCMD;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EmbResourceService extends ActionNoSessionCMD {

    public static final String CMD = "h5.js";

    @Override
    public String getCMD() {
        return CMD;
    }

    /**
     * 注释
     *
     * @param req 注释
     * @param res 注释
     * @return 注释
     */
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ResourceHelper.getH5Js(req, res);
    }
}
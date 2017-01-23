package com.fr.bi.web.base.services;

import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.bi.web.base.ResourceHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIEmbResourceAction extends AbstractBIBaseAction {

    public static final String CMD = "i18n.js";

    @Override
    public String getCMD() {
        return CMD;
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ResourceHelper.geti18nJS(req, res);
    }
}

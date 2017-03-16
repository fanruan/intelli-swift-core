package com.fr.bi.web.base.services;

import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.general.GeneralUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by juhaoyu on 2016/12/27.
 */
public class BIGetBuildNoAction extends AbstractBIBaseAction {

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {

        WebUtils.printAsString(res, GeneralUtils.readBuildNO());
    }

    @Override
    public String getCMD() {

        return "get_build_no";
    }
}
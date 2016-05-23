package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/5/20.
 */
public class BIGetPrimaryTablesByTableAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String table = WebUtils.getHTTPRequestParameter(req, "table");

    }

    @Override
    public String getCMD() {
        return "get_primary_tables_by_table";
    }
}

package com.fr.bi.web.base.services;

import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Coder: Sheldon
 * Date: 4/8/15
 * Time: 7:31 PM
 */
public class BIGetFieldAction extends AbstractBIBaseAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String id = WebUtils.getHTTPRequestParameter(req, "id");

//        WebUtils.printAsJSON(res, BIStableFactory.getDataSourceManager().get(id, userId).createJSON());
    }

    @Override
    public String getCMD() {
        return "get_field";
    }
}
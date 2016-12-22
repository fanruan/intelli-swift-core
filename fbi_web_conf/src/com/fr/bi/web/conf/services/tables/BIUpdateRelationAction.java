package com.fr.bi.web.conf.services.tables;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/12/21.
 */
public class BIUpdateRelationAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {

    }

    @Override
    public String getCMD() {
        return "update_relation";
    }
}

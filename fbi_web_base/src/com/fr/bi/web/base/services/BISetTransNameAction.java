package com.fr.bi.web.base.services;

import com.fr.bi.web.base.AbstractBIBaseAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by GUY on 2015/3/31.
 */
public class BISetTransNameAction extends AbstractBIBaseAction {

    /**
     * trans:[{"id":1,"name":2}];
     *
     * @return
     */
    @Override
    public String getCMD() {
        return "set_trans";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {

    }
}
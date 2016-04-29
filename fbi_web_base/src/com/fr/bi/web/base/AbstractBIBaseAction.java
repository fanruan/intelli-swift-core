package com.fr.bi.web.base;

import com.fr.base.FRContext;
import com.fr.web.core.ActionNoSessionCMD;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractBIBaseAction extends ActionNoSessionCMD {

    /**
     * 执行
     *
     * @param req 传入
     * @param res 传出
     */
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) {
        try {
            actionCMDPrivilegePassed(req, res);
        } catch (Exception e) {
            FRContext.getLogger().errorWithServerLevel(e.getMessage(), e);
            new JSONErrorHandler().error(req, res, e.getMessage());
        }
    }

    protected abstract void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception;
}
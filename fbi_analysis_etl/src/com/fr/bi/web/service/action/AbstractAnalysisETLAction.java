package com.fr.bi.web.service.action;

import com.fr.web.core.ActionCMD;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/4/7.
 */
public abstract class AbstractAnalysisETLAction implements ActionCMD {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        actionCMD(req, res, null);
    }
}

package com.fr.bi.web.conf.services.tables;

import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.services.session.BIConfSessionUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 停止编辑表，用于释放锁
 * Created by Young's on 2016/12/22.
 */
public class BICancelEditTableAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String sessionID = WebUtils.getHTTPRequestParameter(req, "sessionID");
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");

        BIConfSessionUtils.getSession(sessionID).releaseTableLock(tableId);
    }

    @Override
    public String getCMD() {
        return "cancel_edit_table";
    }
}

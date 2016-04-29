package com.fr.bi.web.dezi.services;

import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.SessionIDInfor;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIRemoveWidgetAction extends AbstractBIDeziAction {

    /**
     * 方法
     *
     * @param req       参数1
     * @param res       参数2
     * @param sessionID 当前session
     * @throws Exception
     */
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res,
                          String sessionID) throws Exception {
        SessionIDInfor ss = SessionDealWith.getSessionIDInfor(sessionID);
        if (ss == null || !(ss instanceof BISession)) {
            return;
        }
        BISession session = (BISession) ss;
        String widgetName = WebUtils.getHTTPRequestParameter(req, "widgetName");
        if (widgetName == null) {
            return;
        }
        session.removeBookByName(widgetName);
        BIReport report = session.getBIReport();
        report.removeWidget(widgetName);
    }

    @Override
    public String getCMD() {
        return "widget_remove";
    }

}
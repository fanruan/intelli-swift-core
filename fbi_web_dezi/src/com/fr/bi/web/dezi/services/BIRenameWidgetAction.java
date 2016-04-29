package com.fr.bi.web.dezi.services;

import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.stable.StringUtils;
import com.fr.web.core.ErrorHandlerHelper;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIRenameWidgetAction extends AbstractBIDeziAction {

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res,
                          String sessionID) throws Exception {
        BISession sessionIDInfor = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        if (sessionIDInfor == null) {
            ErrorHandlerHelper.getErrorHandler().error(req, res, "Reportlet SessionID: \"" + sessionID + "\" time out.");
            return;
        }
        String oldName = WebUtils.getHTTPRequestParameter(req, "oldName");
        String newName = WebUtils.getHTTPRequestParameter(req, "newName");
        if (StringUtils.isNotBlank(oldName) && StringUtils.isNotBlank(newName)) {
            BIReport report = sessionIDInfor.getBIReport();
            BIWidget widget = report.getWidgetByName(oldName);
            if (widget != null) {
                //widget.renameWidget(oldName, newName);
                report.renameWidgetName(oldName, newName);
            }
        }
    }

    @Override
    public String getCMD() {
        return "rename_widget";
    }

}
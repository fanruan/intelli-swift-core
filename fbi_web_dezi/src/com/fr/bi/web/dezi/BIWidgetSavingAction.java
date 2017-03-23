package com.fr.bi.web.dezi;

import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.report.BIWidget;
import com.fr.json.JSONObject;
import com.fr.web.core.ErrorHandlerHelper;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zcf on 2017/3/23.
 */
public class BIWidgetSavingAction extends AbstractBIDeziAction {


    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        BISession sessionIDInfor = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        if (sessionIDInfor == null) {
            ErrorHandlerHelper.getErrorHandler().error(req, res, "Reportlet SessionID: \"" + sessionID + "\" time out.");
            return;
        }
        long userId = sessionIDInfor.getUserIdFromSession(req);
        String widgetString = WebUtils.getHTTPRequestParameter(req, "widget");

        JSONObject widgetJSON = new JSONObject(widgetString);
        String widgetName = widgetJSON.optString("name");
        widgetJSON.put("sessionID", sessionID);
        BIWidget widget = BIWidgetFactory.parseWidget(widgetJSON, userId);
        BIReport biReport = sessionIDInfor.getBIReport();
        int index = biReport.getWidgetIndexByName(widgetName);
        biReport.setWidget(index, widget);
    }

    @Override
    public String getCMD() {
        return "save_widget";
    }
}

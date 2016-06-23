package com.fr.bi.web.report.services;

import com.fr.bi.fs.BISharedReportDAO;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.Decrypt;
import com.fr.json.JSONArray;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2015/8/4.
 */
public class BIShareTemplateSaveAction extends ActionNoSessionCMD {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String templateIdsString = WebUtils.getHTTPRequestParameter(req, "reports");
        String userIdsString = WebUtils.getHTTPRequestParameter(req, "users");

        templateIdsString = Decrypt.decrypt(templateIdsString, "neilsx");
        userIdsString = Decrypt.decrypt(userIdsString, "neilsx");
        JSONArray jaTemplateIds = new JSONArray(templateIdsString);
        JSONArray jaUserIds = new JSONArray(userIdsString);
        long[] templateIds = new long[jaTemplateIds.length()];
        long[] userIds = new long[jaUserIds.length()];
        for (int i = 0, len = userIds.length; i < len; i++) {
            userIds[i] = jaUserIds.getLong(i);
        }
        for(int j = 0; j < templateIds.length; j++) {
            UserControl.getInstance().getOpenDAO(BISharedReportDAO.class).resetSharedByReportIdAndUsers(jaTemplateIds.getLong(j), userId, userIds);
        }
    }

    @Override
    public String getCMD() {
        return "template_folder_share";
    }
}
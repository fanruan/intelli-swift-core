package com.fr.bi.web.report.services;

import com.fr.bi.cal.stable.utils.BIReportUtils;
import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by User on 2016/3/31.
 */
public class BIGetWidgetFromTemplateAction extends ActionNoSessionCMD {
    public static final String CMD = "get_widget_from_template";

    @Override
    public String getCMD() {
        return CMD;
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String id = WebUtils.getHTTPRequestParameter(req, "id");
        long currentLoginUserId = ServiceUtils.getCurrentUserID(req);
        BIReportNode node = null;
        if (id != null) {
            node = BIDAOUtils.findByID(Long.parseLong(id), userId);
        }

        if (currentLoginUserId < 0 && currentLoginUserId != UserControl.getInstance().getSuperManagerID()) {
            return;
        }
        if (node == null) {
            throw new Exception("can't find the report! might be delete or move!");
        }
        JSONObject reportSetting = BIReportUtils.getBIReportNodeJSON(node);

        WebUtils.printAsJSON(res, reportSetting.optJSONObject("widgets"));
    }
}

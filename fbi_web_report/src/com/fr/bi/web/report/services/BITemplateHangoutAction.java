package com.fr.bi.web.report.services;

import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/6/1.
 */
public class BITemplateHangoutAction extends ActionNoSessionCMD {

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String id = WebUtils.getHTTPRequestParameter(req, "id");
        String status = WebUtils.getHTTPRequestParameter(req, "status");
        JSONObject jo = new JSONObject();
        try {
            BIReportNode reportNode = BIDAOUtils.findByID(Long.parseLong(id), userId);
            reportNode.setStatus(Integer.parseInt(status));
            BIDAOUtils.saveOrUpDate(reportNode, userId);
            jo.put("result", true);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "report_hangout";
    }
}

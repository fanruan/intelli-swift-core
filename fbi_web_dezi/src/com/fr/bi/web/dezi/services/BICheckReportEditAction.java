package com.fr.bi.web.dezi.services;

import com.fr.bi.cal.analyze.session.BISessionUtils;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/11/29.
 * 检查模板是否正在被编辑
 */
public class BICheckReportEditAction extends AbstractBIDeziAction {
    @Override
    public String getCMD() {
        return "check_report_edit";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String createBy = WebUtils.getHTTPRequestParameter(req, "createBy");
        String reportId = WebUtils.getHTTPRequestParameter(req, "id");

        String editUser = BISessionUtils.getCurrentEditingUserByReport(Long.valueOf(reportId), Long.valueOf(createBy));

        WebUtils.printAsJSON(res, new JSONObject().put("result", editUser == null));
    }
}

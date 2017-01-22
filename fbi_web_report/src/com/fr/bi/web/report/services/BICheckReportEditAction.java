package com.fr.bi.web.report.services;

import com.fr.bi.cal.analyze.session.BISessionUtils;
import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.fs.BITemplateFolderNode;
import com.fr.bi.fs.HSQLBITemplateFolderDAO;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Young's on 2016/11/29.
 * 检查模板是否正在被编辑
 */
public class BICheckReportEditAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "check_report_edit";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String createBy = WebUtils.getHTTPRequestParameter(req, "createBy");
        String reportId = WebUtils.getHTTPRequestParameter(req, "id");
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONArray editUsers = new JSONArray();
        if (createBy == null) {
            getFolderEditing(userId, reportId, editUsers);
        } else {
            String editUser = BISessionUtils.getCurrentEditingUserByReport(Long.valueOf(reportId), Long.valueOf(createBy));
            if (editUser != null) {
                editUsers.put(editUser);
            }
        }

        WebUtils.printAsJSON(res, new JSONObject().put("result", editUsers));
    }

    private void getFolderEditing(long userId, String id, JSONArray editUsers) throws Exception {
        List<BIReportNode> reports = BIDAOUtils.findByParentID(userId, id);
        List<BITemplateFolderNode> folders = HSQLBITemplateFolderDAO.getInstance().findTemplateFolderByParentId(id);
        if (reports != null) {
            for (int i = 0; i < reports.size(); i++) {
                String userName = BISessionUtils.getCurrentEditingUserByReport(reports.get(i).getId(), userId);
                if (userName != null) {
                    editUsers.put(userName);
                }
            }
        }
        for (int i = 0; i < folders.size(); i++) {
            String fId = folders.get(i).getFolderId();
            getFolderEditing(userId, fId, editUsers);
        }
    }
}

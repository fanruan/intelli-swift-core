package com.fr.bi.web.report.services;

import com.fr.bi.fs.*;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 删除模板文件夹
 *
 * Created by Baron on 2015/12/22.
 */
public class BITemplateFolderDeleteAction extends ActionNoSessionCMD {
    private static final int DELETE_FOLDER = 1;
    private static final int DELETE_REPORT = 2;

    @Override
    public String getCMD() {
        return "template_folder_delete";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        String id = WebUtils.getHTTPRequestParameter(req, "id");
        String sType = WebUtils.getHTTPRequestParameter(req, "type");
        long userId = ServiceUtils.getCurrentUserID(req);
        int type = Integer.parseInt(sType);
        switch (type){
            case DELETE_FOLDER:
                removeFolderById(userId, id);
                break;
            case DELETE_REPORT:
                removeReportById(userId, Long.parseLong(id));
                break;
        }
    }

    private void removeReportById(long userId, long reportId) throws Exception {
        BIDAOUtils.deleteBIReportById(userId, reportId);
        UserControl.getInstance().getOpenDAO(BISharedReportDAO.class).removeSharedByReport(reportId, userId);
    }

    private void removeFolderById(long userId, String folderId) throws Exception {
        HSQLBITemplateFolderDAO.getInstance().deleteByFolderID(folderId);
        List<BIReportNode> reports = BIDAOUtils.findByParentID(userId, folderId);
        List<BITemplateFolderNode> folders = HSQLBITemplateFolderDAO.getInstance().findTemplateFolderByParentId(folderId);
        for(int i = 0; i < reports.size(); i++){
            removeReportById(userId, reports.get(i).getId());
        }
        for(int i = 0; i < folders.size(); i++){
            String fId = folders.get(i).getFolderId();
            HSQLBITemplateFolderDAO.getInstance().deleteByFolderID(fId);
            removeFolderById(userId, fId);
        }
    }
}
package com.fr.bi.web.report.services;

import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.fs.BITemplateFolderNode;
import com.fr.bi.fs.HSQLBITemplateFolderDAO;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by Baron on 2015/12/22.
 */
public class BITemplateFolderRenameAction extends ActionNoSessionCMD {
    private static final int RENAME_FOLDER = 1;
    private static final int RENAME_REPORT = 2;
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String id = WebUtils.getHTTPRequestParameter(req, "id");
        String pId = WebUtils.getHTTPRequestParameter(req, "pId");
        String newName = WebUtils.getHTTPRequestParameter(req, "name");
        String sType = WebUtils.getHTTPRequestParameter(req, "type");
        long userId = ServiceUtils.getCurrentUserID(req);
        int type = Integer.parseInt(sType);

        switch (type){
            case RENAME_FOLDER:
                BITemplateFolderNode node = HSQLBITemplateFolderDAO.getInstance().findFolderByFolderId(id, userId);
                if(node != null) {
                    node.setFolderName(newName);
                    node.setLastModifyTime(new Date());
                    HSQLBITemplateFolderDAO.getInstance().saveOrUpdate(node);
                } else {
                    BITemplateFolderNode folderNode = new BITemplateFolderNode();
                    folderNode.setUserId(userId);
                    folderNode.setParentId(pId);
                    folderNode.setFolderId(id);
                    folderNode.setFolderName(newName);
                    folderNode.setLastModifyTime(new Date());
                    HSQLBITemplateFolderDAO.getInstance().saveOrUpdate(folderNode);
                }
                break;
            case RENAME_REPORT:
                BIReportNode reportNode = BIDAOUtils.findByID(Long.parseLong(id), userId);
                reportNode.setReportName(newName);
                BIDAOUtils.saveOrUpDate(reportNode, userId);
                break;
        }
    }

    @Override
    public String getCMD() {
        return "template_folder_rename";
    }

}
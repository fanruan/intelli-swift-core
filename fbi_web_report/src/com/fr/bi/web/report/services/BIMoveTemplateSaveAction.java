package com.fr.bi.web.report.services;

import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.fs.BITemplateFolderNode;
import com.fr.bi.fs.HSQLBITemplateFolderDAO;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Baron on 2015/12/22.
 */
public class BIMoveTemplateSaveAction extends ActionNoSessionCMD {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String selectedFolders = WebUtils.getHTTPRequestParameter(req, "selected_folders");
        String toFolder = WebUtils.getHTTPRequestParameter(req, "to_folder");
        long userId = ServiceUtils.getCurrentUserID(req);

        JSONArray folderIds = new JSONArray(selectedFolders);
        for(int i = 0; i < folderIds.length(); i++) {
            String id = folderIds.getString(i);
            //先判断该节点是文件夹节点 还是模板节点
            BITemplateFolderNode node = HSQLBITemplateFolderDAO.getInstance().findFolderByFolderId(id, userId);
            if(node != null){
                node.setParentId(toFolder);
                HSQLBITemplateFolderDAO.getInstance().saveOrUpdate(node);
            } else {
                BIReportNode reportNode = BIDAOUtils.findByID( Long.parseLong(id), userId);
                reportNode.setParentid(toFolder);
                BIDAOUtils.saveOrUpDate(reportNode, userId);
            }
        }
    }

    @Override
    public String getCMD() {
        return "template_folder_move";
    }
}
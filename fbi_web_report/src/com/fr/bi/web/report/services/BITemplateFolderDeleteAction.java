package com.fr.bi.web.report.services;

import com.fr.bi.cal.analyze.session.BISessionUtils;
import com.fr.bi.fs.*;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
        List<String> lockedList = new ArrayList<String>();
        switch (type){
            case DELETE_FOLDER:
                lockedList = removeFolderById(userId, id);
                break;
            case DELETE_REPORT:
                String userName = removeReportById(userId, Long.parseLong(id));
                if(userName != null) {
                    lockedList.add(userName);
                }
                break;
        }
        JSONArray userList = new JSONArray();
        for(int i = 0; i < lockedList.size(); i++) {
            userList.put(lockedList.get(i));
        }
        WebUtils.printAsJSON(res, userList);
    }

    private String removeReportById(long userId, long reportId) throws Exception {
        String userName = BISessionUtils.getCurrentEditingUserByReport(reportId, userId);
        if(userName == null) {
            BIDAOUtils.getBIDAOManager().deleteBIReportById(userId, reportId);
            UserControl.getInstance().getOpenDAO(BISharedReportDAO.class).removeSharedByReport(reportId, userId);
        }
        return userName;
    }

    private List<String> removeFolderById(long userId, String folderId) throws Exception {
        List<BIReportNode> reports = BIDAOUtils.getBIDAOManager().findByParentID(userId, folderId);
        List<BITemplateFolderNode> folders = HSQLBITemplateFolderDAO.getInstance().findTemplateFolderByParentId(folderId);
        List<String> lockedList = new ArrayList<String>();
        if(reports != null) {
            for(int i = 0; i < reports.size(); i++){
                String userName = removeReportById(userId, reports.get(i).getId());
                if(userName != null) {
                    lockedList.add(userName);
                }
            }
        }
        for(int i = 0; i < folders.size(); i++){
            String fId = folders.get(i).getFolderId();
            List<String> singleList = removeFolderById(userId, fId);
            lockedList.addAll(singleList);
        }
        if(lockedList.size() == 0) {
            HSQLBITemplateFolderDAO.getInstance().deleteByFolderID(folderId);
        }
        return lockedList;
    }
}
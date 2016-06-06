package com.fr.bi.web.report.services;

import com.fr.bi.fs.*;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
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
                HSQLBITemplateFolderDAO.getInstance().deleteByFolderID(id);
                JSONObject jo = getFolderChildrenByFolderId(userId, id);
                JSONArray reports = jo.getJSONArray("reports");
                JSONArray folders = jo.getJSONArray("folders");
                for(int i = 0; i < reports.length(); i++){
                    long reportId = reports.optLong(i);
                    BIDAOUtils.deleteBIReportById(userId, reportId);
                    //删除的时候同时清一下share
                    UserControl.getInstance().getOpenDAO(BISharedReportDAO.class).removeSharedByReport(reportId, userId);
                }
                for(int i = 0; i < folders.length(); i++){
                    HSQLBITemplateFolderDAO.getInstance().deleteByFolderID(folders.getString(i));
                }
                break;
            case DELETE_REPORT:
                long reportId = Long.parseLong(id);
                BIDAOUtils.deleteBIReportById(userId, reportId);
                UserControl.getInstance().getOpenDAO(BISharedReportDAO.class).removeSharedByReport(reportId, userId);
                break;
        }
    }

    private JSONObject getFolderChildrenByFolderId(long userId, String folderId) throws Exception{
        JSONObject jo = new JSONObject();
        JSONArray reportsJA = new JSONArray();
        JSONArray foldersJA = new JSONArray();
        List<BIReportNode> reports = BIDAOUtils.findByParentID(userId, folderId);
        List<BITemplateFolderNode> folders = HSQLBITemplateFolderDAO.getInstance().findTemplateFolderByParentId(folderId);
        for(int i = 0; i < reports.size(); i++){
            reportsJA.put(reports.get(i).getId());
        }
        for(int i = 0; i < folders.size(); i++){
            String fId = folders.get(i).getFolderId();
            foldersJA.put(fId);
            JSONObject children = getFolderChildrenByFolderId(userId, fId);
            reportsJA.put(children.getJSONArray("reports"));
            foldersJA.put(children.getJSONArray("folders"));
        }
        jo.put("reports", reportsJA);
        jo.put("folders", foldersJA);
        return jo;
    }

}
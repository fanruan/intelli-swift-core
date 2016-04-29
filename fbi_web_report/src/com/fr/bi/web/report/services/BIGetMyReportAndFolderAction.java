package com.fr.bi.web.report.services;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Young's on 2016/1/26.
 */
public class BIGetMyReportAndFolderAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "get_my_report_and_folder";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        List<BIReportNode> nodeList = BIDAOUtils.findByUserID(userId);
        JSONArray reports = new JSONArray();
        for(int i = 0; i < nodeList.size(); i++){
            reports.put(nodeList.get(i).getReportName());
        }

        List<BITemplateFolderNode> folderList = HSQLBITemplateFolderDAO.getInstance().findFolderByUserID(userId);
        if(folderList == null) {
            folderList = new ArrayList<BITemplateFolderNode>();
        }

        JSONArray folderArray = new JSONArray();
        //我创建的
        for(int i = 0; i < folderList.size(); i++){
            JSONObject folder = new JSONObject();
            folder.put("id", folderList.get(i).getFolderId());
            folder.put("pId", folderList.get(i).getParentId());
            folder.put("text", folderList.get(i).getFolderName());
            folder.put("value", folderList.get(i).getFolderId());
            folderArray.put(folder);
        }
        JSONObject jo = new JSONObject();
        jo.put("reports", reports);
        jo.put("folders", folderArray);
        WebUtils.printAsJSON(res, jo);
    }
}
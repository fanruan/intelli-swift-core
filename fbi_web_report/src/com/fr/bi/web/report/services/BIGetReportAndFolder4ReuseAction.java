package com.fr.bi.web.report.services;

import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.fs.BITemplateFolderNode;
import com.fr.bi.fs.HSQLBITemplateFolderDAO;
import com.fr.bi.stable.utils.algorithem.BISortUtils;
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
 * Created by Young's on 2016/7/14.
 */
public class BIGetReportAndFolder4ReuseAction extends ActionNoSessionCMD {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONArray ja = new JSONArray();
        List<BITemplateFolderNode> folderList = HSQLBITemplateFolderDAO.getInstance().findFolderByUserID(userId);
        for(int i = 0; i < folderList.size(); i++){
            ja.put(folderList.get(i).createJSONConfig());
        }

        List<BIReportNode> nodeList = BIDAOUtils.findByUserID(userId);
        BISortUtils.sortByModifyTime(nodeList);
        if (nodeList == null) {
            nodeList = new ArrayList<BIReportNode>();
        }
        for(int i = 0; i < nodeList.size(); i++){
            BIReportNode node = nodeList.get(i);
            JSONObject nodeJO = node.createJSONConfig();
            ja.put(nodeJO);
        }
        WebUtils.printAsJSON(res, ja);
    }

    @Override
    public String getCMD() {
        return "get_folder_report_list_4_reuse";
    }
}

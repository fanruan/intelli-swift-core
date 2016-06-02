package com.fr.bi.web.report.services;

import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.fs.BITemplateFolderNode;
import com.fr.bi.fs.HSQLBITemplateFolderDAO;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.algorithem.BISortUtils;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
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
 * Created by Young's on 2016/6/2.
 */
public class BIGetAllHangoutReportsAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "get_all_hangout_reports";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject resJO = new JSONObject();
        if (userId == UserControl.getInstance().getSuperManagerID()) {
            JSONObject jo = new JSONObject();

            //管理员的模板和文件夹
            JSONArray ja = new JSONArray();
            List<BITemplateFolderNode> folderList = HSQLBITemplateFolderDAO.getInstance().findFolderByUserID(userId);
            for (int i = 0; i < folderList.size(); i++) {
                ja.put(folderList.get(i).createJSONConfig());
            }

            List<BIReportNode> nodeList = BIDAOUtils.findByUserID(userId);
            BISortUtils.sortByModifyTime(nodeList);
            if (nodeList == null) {
                nodeList = new ArrayList<BIReportNode>();
            }
            for (int i = 0; i < nodeList.size(); i++) {
                ja.put(nodeList.get(i).createJSONConfig());
            }
            jo.put(String.valueOf(UserControl.getInstance().getSuperManagerID()), ja);

            //普通用户所有的申请中和已挂出模板
            List<User> users = UserControl.getInstance().findAllUser();
            JSONObject usersJO = new JSONObject();
            for (int i = 0; i < users.size(); i++) {
                long uId = users.get(i).getId();
                String userName = users.get(i).getRealname();
                usersJO.put(String.valueOf(uId), userName);
                List<BIReportNode> reports = BIDAOUtils.findByUserID(uId);
                JSONArray reportsJA = new JSONArray();
                for (int j = 0; j < reports.size(); j++) {
                    BIReportNode node = reports.get(j);
                    if (node.getStatus() != BIReportConstant.REPORT_STATUS.NORMAL) {
                        reportsJA.put(node.createJSONConfig());
                    }
                }
                jo.put(String.valueOf(uId), reportsJA);
            }

            resJO.put("all_reports", jo);
            resJO.put("users", usersJO);
        }

        WebUtils.printAsJSON(res, resJO);
    }
}

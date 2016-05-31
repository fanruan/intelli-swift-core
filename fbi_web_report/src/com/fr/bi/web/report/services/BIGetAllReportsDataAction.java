package com.fr.bi.web.report.services;

import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.DepartmentControl;
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
 * Created by Young's on 2016/5/30.
 */
public class BIGetAllReportsDataAction extends ActionNoSessionCMD {

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        //需要拿到的信息 所有的角色、人员信息用于过滤 所有的模板详细信息
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject jo = new JSONObject();
        if (userId == UserControl.getInstance().getSuperManagerID()) {
            JSONArray departs = DepartmentControl.getInstance().getAllDepartmentInfo(true);
            JSONArray roles = UserControl.getInstance().getAllDepAndCRoleInfo(userId);
            JSONArray users = new JSONArray();
            JSONArray reports = new JSONArray();
            List<User> userList = UserControl.getInstance().findAllUser();
            for(int i = 0; i < userList.size(); i++){
                User u = userList.get(i);
                users.put(u.createEditInfoJSONConfig());
                List<BIReportNode> singleUserReports = BIDAOUtils.findByUserID(u.getId());
                for(int j = 0; j < singleUserReports.size(); j++){
                    reports.put(singleUserReports.get(j).createJSONConfig());
                }
            }
            jo.put("departs", departs);
            jo.put("roles", roles);
            jo.put("users", users);
            jo.put("reports", reports);
        }
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_all_reports_data";
    }
}

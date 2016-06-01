package com.fr.bi.web.report.services;

import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.CompanyRoleControl;
import com.fr.fs.control.CustomRoleControl;
import com.fr.fs.control.DepartmentControl;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
            JSONArray companyRoles = CompanyRoleControl.getInstance().getAllCompanyRoleInfo();
            JSONArray customRoles = CustomRoleControl.getInstance().getAllCustomRoleInfo();
            for(int i = 0; i < companyRoles.length(); i++){
                JSONObject cRole = companyRoles.getJSONObject(i);
                int rId = cRole.getInt("id");
                Set<Long> uIds = CompanyRoleControl.getInstance().getUsersID(rId);
                Iterator<Long> it = uIds.iterator();
                JSONArray users = new JSONArray();
                while (it.hasNext()) {
                    users.put(it.next());
                }
                cRole.put("users", users);
            }
            for(int i = 0; i < customRoles.length(); i++){
                JSONObject cRole = customRoles.getJSONObject(i);
                int rId = cRole.getInt("id");
                Set<Long> uIds = CustomRoleControl.getInstance().getUsersID(rId);
                Iterator<Long> it = uIds.iterator();
                JSONArray users = new JSONArray();
                while (it.hasNext()) {
                    users.put(it.next());
                }
                cRole.put("users", users);
                companyRoles.put(cRole);
            }

            JSONArray users = new JSONArray();
            JSONArray reports = new JSONArray();
            List<User> userList = UserControl.getInstance().findAllUser();
            userList.add(UserControl.getInstance().getUser(UserControl.getInstance().getSuperManagerID()));
            for(int i = 0; i < userList.size(); i++){
                User u = userList.get(i);
                users.put(u.createEditInfoJSONConfig());
                List<BIReportNode> singleUserReports = BIDAOUtils.findByUserID(u.getId());
                for(int j = 0; j < singleUserReports.size(); j++){
                    reports.put(singleUserReports.get(j).createJSONConfig());
                }
            }
            jo.put("departs", departs);
            jo.put("roles", companyRoles);
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

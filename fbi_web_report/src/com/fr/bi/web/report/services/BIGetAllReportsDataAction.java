package com.fr.bi.web.report.services;

import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.data.dao.RelationObject;
import com.fr.fs.base.entity.CustomRole;
import com.fr.fs.base.entity.Department;
import com.fr.fs.base.entity.Post;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.*;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
            List<User> userHasBIReportNode = new LinkedList<User>();
            JSONArray users = new JSONArray();
            JSONArray reports = new JSONArray();
            List<User> userList = UserControl.getInstance().findAllUser();
            JSONArray allEntry = EntryControl.getInstance().getRootNode().createAllEntryJSONArray(UserControl.getInstance().getSuperManagerID(), true);
            for (int i = 0; i < userList.size(); i++) {//管理员查看所有模板，只选择有模板的用户
                User u = userList.get(i);
                List<BIReportNode> singleUserReports = BIDAOUtils.findByUserID(u.getId());
                if (singleUserReports.size() > 0) {
                    userHasBIReportNode.add(u);//添加有模板的用户
                    users.put(u.createEditInfoJSONConfig());
                }
                for (int j = 0; j < singleUserReports.size(); j++) {
                    BIReportNode node = singleUserReports.get(j);
                    //TODO 在这里去check status不合适，然而在模板管理的地方删除模板也不好处理bi模板的状态问题
                    if (node.getStatus() == BIReportConstant.REPORT_STATUS.HANGOUT &&
                            checkReportStatus(node.getId(), node.getUserId(), allEntry)) {
                        node.setStatus(BIReportConstant.REPORT_STATUS.NORMAL);
                    }
                    reports.put(node.createJSONConfig());
                }
            }
            JSONArray departs = new JSONArray();//保存有模板用户对应的部门信息
            Set<Long> departments = new HashSet<Long>();//保存有模板用户对应的部门id，然后用这个id来查询部门，生成部门的jsonobject，并添加到departs的数值中
            JSONArray companyRoles = CompanyRoleControl.getInstance().getAllCompanyRoleInfo();//所有部门角色信息
            JSONArray usefulCompanyRoles = new JSONArray();//这个用来保存有模板用户的所对应的职位角色
            addUsers2CompanyRoleAndCustomRole(userHasBIReportNode, companyRoles, usefulCompanyRoles, departments);//添加对应角色用户
            for (Long depId : departments) {
                Department department = DepartmentControl.getInstance().getDepartment(depId);
                if (department != null) {
                    departs.put(department.createJSONConfig());
                }
            }
            jo.put("departs", departs);
            jo.put("roles", usefulCompanyRoles);
            jo.put("users", users);
            jo.put("reports", reports);
        }
        WebUtils.printAsJSON(res, jo);
    }

    private void addUsers2CompanyRoleAndCustomRole(List<User> userHasBIReportNode, JSONArray companyRoles, JSONArray usefulCompanyRoles, Set<Long> departments) throws Exception {
        Map<Long, JSONObject> jsonCustomRoleArrayIndex = new HashMap<Long, JSONObject>();//保存有模板用户对应的customRole，key为customRole的Id，value是customRole对应的JSONObject
        for (User user : userHasBIReportNode) {
            Set<RelationObject> userJob = UserControl.getInstance().getUserJobSetByUserId(user.getId());
            for (RelationObject job : userJob) {
                for (int i = 0; i < companyRoles.length(); i++) {
                    JSONObject companyRole = companyRoles.getJSONObject(i);
                    if (ComparatorUtils.equals((Long) (job.getValue(Department.class)), companyRole.getLong("departmentid")) && ComparatorUtils.equals((Long) (job.getValue(Post.class)), companyRole.getLong("postid"))) {
                        JSONArray company = companyRole.optJSONArray("users");
                        if (company == null) {
                            JSONArray userArray = new JSONArray();
                            userArray.put(user.getId());
                            companyRole.put("users", userArray);
                            usefulCompanyRoles.put(companyRole);
                            departments.add(companyRole.getLong("departmentid"));
                        } else {
                            company.put(user.getId());
                        }
                        break;
                    }
                }
            }
            Set<RelationObject> userCustomRole = UserControl.getInstance().getUserRoleSetByUserId(user.getId());
            for (RelationObject customRole : userCustomRole) {
                JSONObject customRoleJson = jsonCustomRoleArrayIndex.get((Long) (customRole.getValue(CustomRole.class)));
                if (customRoleJson == null) {
                    CustomRole custom = CustomRoleControl.getInstance().getCustomRole((Long) (customRole.getValue(CustomRole.class)));
                    customRoleJson = custom.createJSONConfig();
                    JSONArray userArray = new JSONArray();
                    userArray.put(user.getId());
                    customRoleJson.put("users", userArray);
                    usefulCompanyRoles.put(customRoleJson);
                    jsonCustomRoleArrayIndex.put(custom.getId(), customRoleJson);
                } else {
                    JSONArray userJson = customRoleJson.optJSONArray("users");
                    if (userJson == null) {
                        userJson = new JSONArray();
                        userJson.put(user.getId());
                        customRoleJson.put("users", userJson);
                    } else {
                        userJson.put(user.getId());
                    }
                }
            }
        }
    }

    private boolean checkReportStatus(long reportId, long createBy, JSONArray entries) throws Exception {
        boolean needReset = true;
        for (int i = 0; i < entries.length(); i++) {
            JSONObject entry = entries.getJSONObject(i);
            JSONArray childNodes = entry.optJSONArray("ChildNodes");
            if (childNodes != null && checkReportStatus(reportId, createBy, childNodes) == false) {
                needReset = false;
                break;
            }
            long rId = entry.optLong("reportId");
            long uId = entry.optLong("createBy");
            if (reportId == rId && uId == createBy) {
                needReset = false;
                break;
            }
        }
        return needReset;

    }

    @Override
    public String getCMD() {
        return "get_all_reports_data";
    }
}

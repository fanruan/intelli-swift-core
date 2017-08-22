package com.fr.bi.fs;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.algorithem.BISortUtils;
import com.fr.data.dao.RelationObject;
import com.fr.fs.base.entity.CustomRole;
import com.fr.fs.base.entity.Department;
import com.fr.fs.base.entity.Post;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.*;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.bridge.StableFactory;
import org.slf4j.Logger;

import java.util.*;

/**
 * Created by wang on 2017/4/20.
 */
public class BIReportQueryManger implements BIReportQueryProvider {
    private static final Logger logger = BILoggerFactory.getLogger(BIReportQueryManger.class);
    public static final String XML_TAG = "BIReportQueryManger";
    private static BIReportQueryManger manager;

    public static BIReportQueryProvider getProviderInstance() {
        return StableFactory.getMarkedObject(BIReportQueryProvider.XML_TAG, BIReportQueryProvider.class);
    }

    public static BIReportQueryManger getInstance() {
        synchronized (BIReportQueryManger.class) {
            if (manager == null) {
                manager = new BIReportQueryManger();
            }
            return manager;
        }
    }

    @Override
    public JSONObject getAllHangoutReports(long userId, String currentUser) {
        JSONObject resJO = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            //管理员的模板和文件夹
            JSONArray ja = new JSONArray();
            List<BITemplateFolderNode> folderList = HSQLBITemplateFolderDAO.getInstance().findFolderByUserID(userId);
            for (int i = 0; i < folderList.size(); i++) {
                ja.put(folderList.get(i).createJSONConfig());
            }
            List<BIReportNode> nodeList = BIDAOUtils.getBIDAOManager().findByUserID(userId);
            BISortUtils.sortByModifyTime(nodeList);
            if (nodeList == null) {
                nodeList = new ArrayList<BIReportNode>();
            }
            for (int i = 0; i < nodeList.size(); i++) {
                ja.put(nodeList.get(i).createJSONConfig());
            }
            jo.put(currentUser, ja);
            //普通用户所有的申请中和已挂出模板
            List<User> users = UserControl.getInstance().findAllUser();
            JSONObject usersJO = new JSONObject();
            for (int i = 0; i < users.size(); i++) {
                long uId = users.get(i).getId();
                if (!ComparatorUtils.equals(userId, uId)) {
                    String userName = users.get(i).getRealname();
                    usersJO.put(String.valueOf(uId), userName);
                    List<BIReportNode> reports = BIDAOUtils.getBIDAOManager().findByUserID(uId);
                    JSONArray reportsJA = new JSONArray();
                    for (int j = 0; j < reports.size(); j++) {
                        BIReportNode node = reports.get(j);
                        if (node.getStatus() != BIReportConstant.REPORT_STATUS.NORMAL) {
                            reportsJA.put(node.createJSONConfig());
                        }
                    }
                    jo.put(String.valueOf(uId), reportsJA);
                }
            }
            resJO.put("allReports", jo);
            resJO.put("users", usersJO);
        } catch (Exception e) {
            logger.error("getAllHangoutReports exception :", e);
        }
        return resJO;
    }

    @Override
    public JSONArray getReportAndFolder(long userId) {
        JSONArray ja = new JSONArray();
        try {
            List<BITemplateFolderNode> folderList = HSQLBITemplateFolderDAO.getInstance().findFolderByUserID(userId);
            for (int i = 0; i < folderList.size(); i++) {
                ja.put(folderList.get(i).createJSONConfig());
            }
            List<BIReportNode> nodeList = BIDAOUtils.getBIDAOManager().findByUserID(userId);
            BISortUtils.sortByModifyTime(nodeList);
            if (nodeList == null) {
                nodeList = new ArrayList<BIReportNode>();
            }
            JSONArray allEntry = EntryControl.getInstance().getRootNode().createAllEntryJSONArray(UserControl.getInstance().getSuperManagerID(), true);

            for (int i = 0; i < nodeList.size(); i++) {
                BIReportNode node = nodeList.get(i);
            /*处理授权文件被删除的情况时用到*/
//            node.setParentid("-1");
//            BIDAOUtils.saveOrUpDate(node, userId);
                if (node.getStatus() == BIReportConstant.REPORT_STATUS.HANGOUT &&
                        checkReportStatus(node.getId(), node.getUserId(), allEntry)) {
                    node.setStatus(BIReportConstant.REPORT_STATUS.NORMAL);
                }
                JSONObject nodeJO = node.createJSONConfig();
                nodeJO.put("shared", getSharedUsers(node.getId(), userId));
                ja.put(nodeJO);
            }
        } catch (Exception e) {
            logger.error("getReportAndFolder exception :", e);
        }
        return ja;
    }

    @Override
    public JSONObject getAllReportsData(long userId) {
        JSONObject jo = new JSONObject();
        try {
            if (userId == UserControl.getInstance().getSuperManagerID()) {
                List<User> userHasBIReportNode = new LinkedList<User>();
                JSONArray users = new JSONArray();
                JSONArray reports = new JSONArray();
                List<User> userList = UserControl.getInstance().findAllUser();
                JSONArray allEntry = EntryControl.getInstance().getRootNode().createAllEntryJSONArray(UserControl.getInstance().getSuperManagerID(), true);
                for (int i = 0; i < userList.size(); i++) {//管理员查看所有模板，只选择有模板的用户
                    User u = userList.get(i);
                    users.put(u.createEditInfoJSONConfig());
                    List<BIReportNode> singleUserReports = BIDAOUtils.getBIDAOManager().findByUserID(u.getId());
                    if (singleUserReports.size() > 0) {
                        userHasBIReportNode.add(u);//添加有模板的用户
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
        } catch (Exception e) {
            logger.error("getAllReportsData exception :", e);
        }
        return jo;
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

    private JSONArray getSharedUsers(long reportId, long createBy) throws Exception {
        List<User> users = BIDAOUtils.getBIDAOManager().getSharedUsersByReport(reportId, createBy);
        JSONArray ja = new JSONArray();
        if (users != null) {
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                if (user.getId() != createBy) {
                    JSONObject jo = user.createJSON4Share();
                    jo.put("roles", UserControl.getInstance().getAllSRoleNames(user.getId()));
                    ja.put(jo);
                }
            }
        }
        return ja;
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
}

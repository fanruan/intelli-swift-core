package com.fr.bi.stable.utils;

import com.fr.bi.base.provider.AllUserTravel;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.fs.base.entity.CompanyRole;
import com.fr.fs.base.entity.CustomRole;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.*;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.Set;

/**
 * Created by Connery on 2015/12/4.
 */
public class BIUserUtils {

    public static boolean allUserTravelAction(AllUserTravel travel) {
        if (travel != null) {
            travel.start(UserControl.getInstance().getSuperManagerID());
        }
        return true;
    }

    /**
     * 是否使用管理员的设置
     *
     * @param userId 当前登录Id
     * @return
     */
    public static boolean isAdministrator(long userId) {
        return userId == UserControl.getInstance().getSuperManagerID() || BIBaseConstant.BI_MODEL == BIBaseConstant.CLASSIC_BI;
    }

    public static JSONObject createUserJO(long userId) throws Exception {
        User user = UserControl.getInstance().getUser(userId);
        JSONObject jo = JSONObject.create();
        jo.put("id", user.getId());
        jo.put("text", user.getUsername() + "(" + user.getRealname() + ")");
        jo.put("userName", user.getUsername());
        jo.put("realName", user.getRealname());
        return jo;
    }

    public static JSONArray getUserRoleInfo(long userId) throws Exception {
        JSONArray ja = JSONArray.create();
        long departAllId = CompanyRoleControl.getInstance().getDepartmentAllID();
        long postAllId = CompanyRoleControl.getInstance().getPostAllID();
        Set<CustomRole> customRoles = CustomRoleControl.getInstance().getCustomRoleSet(userId);
        Set<CompanyRole> companyRoles = CompanyRoleControl.getInstance().getCompanyRoleSet(userId);
        for (CompanyRole companyRole : companyRoles) {
            if (companyRole.getDepartmentId() != departAllId && companyRole.getPostId() != postAllId) {
                JSONObject jo = companyRole.createJSONConfig();
                jo.put("type", BIBaseConstant.ROLE_TYPE.COMPANY);
                jo.put("departmentname", DepartmentControl.getInstance().getDepartmentShowName(companyRole.getDepartmentId()));
                jo.put("postname", PostControl.getInstance().getPostName(companyRole.getPostId()));
                ja.put(jo);
            }
        }

        for (CustomRole customRole : customRoles) {
            JSONObject jo = customRole.createJSONConfig();
            jo.put("type", BIBaseConstant.ROLE_TYPE.CUSTOM);
            ja.put(jo);
        }
        return ja;
    }
}
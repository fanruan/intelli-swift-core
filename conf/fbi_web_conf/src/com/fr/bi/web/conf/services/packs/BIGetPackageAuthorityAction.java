package com.fr.bi.web.conf.services.packs;

import com.fr.bi.conf.provider.BIAuthorityManageProvider;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.FSConfig;
import com.fr.fs.base.entity.CompanyRole;
import com.fr.fs.control.*;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by kary on 16/4/26.
 */
public class BIGetPackageAuthorityAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);

        BIAuthorityManageProvider authorityManager = BIConfigureManagerCenter.getAuthorityManager();

        JSONArray rolesJA = new JSONArray();

        if (userId == UserControl.getInstance().getSuperManagerID()) {
            List<CompanyRole> roles = CompanyRoleControl.getInstance().getAllCompanyRole();
            for (int i = 0; i < roles.size(); i++) {
                CompanyRole role = roles.get(i);
                JSONObject roleJO = role.createJSONConfig();
                String departName = DepartmentControl.getInstance().getDepartmentShowName(role.getDepartmentId(), ",");
                String postName = PostControl.getInstance().getPostName(role.getPostId());
                if (departName != null && postName != null) {
                    roleJO.put("departmentname", departName);
                    roleJO.put("postname", postName);
                    roleJO.put("role_type", BIBaseConstant.ROLE_TYPE.COMPANY);
                    rolesJA.put(roleJO);
                }
            }

            JSONArray customRoles = CustomRoleControl.getInstance().getAllCustomRoleInfo();
            for (int i = 0; i < customRoles.length(); i++) {
                JSONObject role = customRoles.getJSONObject(i);
                role.put("role_type", BIBaseConstant.ROLE_TYPE.CUSTOM);
                rolesJA.put(role);
            }
        } else if (FSConfig.getProviderInstance().getAuthorizeAttr().isGradeAuthority()) {
            rolesJA = UserControl.getInstance().getAllDepAndCRoleInfo(userId);
            for (int i = 0; i < rolesJA.length(); i++) {
                JSONObject roleJO = rolesJA.getJSONObject(i);
                roleJO.put("role_type", roleJO.opt("departmentname") != null ?
                        BIBaseConstant.ROLE_TYPE.COMPANY : BIBaseConstant.ROLE_TYPE.CUSTOM);
            }
        }

        JSONObject jo = new JSONObject();
        jo.put("packages_auth", authorityManager.createJSON(userId));
        jo.put("all_roles", rolesJA);
        jo.put("login_field", BIConfigureManagerCenter.getCubeConfManager().getLoginField());

        WebUtils.printAsJSON(res, jo);

    }

    @Override
    public String getCMD() {
        return "get_authority_settings";
    }
}

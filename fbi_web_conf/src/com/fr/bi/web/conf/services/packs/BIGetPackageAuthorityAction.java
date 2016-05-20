package com.fr.bi.web.conf.services.packs;

import com.fr.bi.conf.provider.BIAuthorityManageProvider;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.base.entity.CompanyRole;
import com.fr.fs.control.CompanyRoleControl;
import com.fr.fs.control.CustomRoleControl;
import com.fr.fs.control.DepartmentControl;
import com.fr.fs.control.PostControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by wuk on 16/4/26.
 */
public class BIGetPackageAuthorityAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);

        BIAuthorityManageProvider authorityManager = BIConfigureManagerCenter.getAuthorityManager();
        List<CompanyRole> roles = CompanyRoleControl.getInstance().getAllCompanyRole();

        JSONArray rolesJA = new JSONArray();
        for(int i = 0; i < roles.size(); i++) {
            CompanyRole role = roles.get(i);
            JSONObject roleJO = role.createJSONConfig();
            String departName = DepartmentControl.getInstance().getDepartmentShowName(role.getDepartmentId(), "-");
            String postName = PostControl.getInstance().getPostName(role.getPostId());
            if(departName != null && postName != null) {
                roleJO.put("department_name", departName);
                roleJO.put("post_name", postName);
                roleJO.put("role_type", BIBaseConstant.ROLE_TYPE.COMPANY);
                rolesJA.put(roleJO);
            }
        }

        JSONArray customRoles = CustomRoleControl.getInstance().getAllCustomRoleInfo();
        for(int i = 0; i < customRoles.length(); i++) {
            JSONObject role = customRoles.getJSONObject(i);
            role.put("role_type", BIBaseConstant.ROLE_TYPE.CUSTOM);
            rolesJA.put(role);
        }

        JSONObject jo = new JSONObject();
        jo.put("packages_auth", authorityManager.createJSON(userId));
        jo.put("all_roles", rolesJA);
        jo.put("login_info", BIConfigureManagerCenter.getCubeConfManager().getLoginInfoInTableField().createJSON());

        WebUtils.printAsJSON(res, jo);

    }

    @Override
    public String getCMD() {
        return "get_authority_settings";
    }
}

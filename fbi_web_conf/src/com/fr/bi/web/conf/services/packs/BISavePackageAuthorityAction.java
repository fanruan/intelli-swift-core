package com.fr.bi.web.conf.services.packs;

import com.fr.bi.conf.base.pack.data.BIPackAndAuthority;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BISystemPackAndAuthConfigurationProvider;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BISavePackageAuthorityAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "save_package_authority";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        String packageIds = WebUtils.getHTTPRequestParameter(req, "package_ids");
        String roles = WebUtils.getHTTPRequestParameter(req, "roles");
        long userId = ServiceUtils.getCurrentUserID(req);
        savePackageAuthority(packageIds, roles, userId);
    }

    /**
     * 保存业务包权限
     * @param packageIds 业务包名字
     * @param roles     权限的字符串
     * @throws Exception
     */
    private void savePackageAuthority(String packageIds, String roles, long userId) throws Exception {
        JSONArray rolesJA = new JSONArray(roles);
        JSONArray pIdsJA = new JSONArray(packageIds);

        String[] rolesArray = new String[rolesJA.length()];
        for (int i = 0; i < rolesJA.length(); i++) {
            rolesArray[i] = String.valueOf(rolesJA.getString(i));
        }
        BISystemPackAndAuthConfigurationProvider packageAndAuthorityManager = BIConfigureManagerCenter.getPackageAndAuthorityManager();

        for (int i = 0; i < pIdsJA.length(); i++) {
            BIPackAndAuthority biPackAndAuthority = new BIPackAndAuthority();
            biPackAndAuthority.setBiPackageID(String.valueOf(pIdsJA.getString(i)));
            biPackAndAuthority.setRoleIdArray(rolesArray);

            boolean isExisted = packageAndAuthorityManager.containPackage(userId, biPackAndAuthority);
            if (isExisted) {
                packageAndAuthorityManager.updateAuthority(userId, biPackAndAuthority);
            } else {
                packageAndAuthorityManager.addPackage(userId, biPackAndAuthority);
            }
        }
        packageAndAuthorityManager.persistData(userId);
    }
}

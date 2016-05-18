package com.fr.bi.web.conf.services.packs;

import com.fr.bi.conf.base.auth.data.BIPackageAuthority;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.provider.BIAuthorityManageProvider;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

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
        List<BIPackageAuthority> authorities = new ArrayList<BIPackageAuthority>();
        for(int i = 0; i < rolesJA.length(); i++) {
            BIPackageAuthority authority = new BIPackageAuthority();
            authority.parseJSON(rolesJA.getJSONObject(i));
            authorities.add(authority);
        }

        BIAuthorityManageProvider packageAndAuthorityManager = BIConfigureManagerCenter.getAuthorityManager();
        for (int i = 0; i < pIdsJA.length(); i++) {
            packageAndAuthorityManager.savePackageAuth(new BIPackageID(pIdsJA.getString(i)), authorities, userId);
        }
        packageAndAuthorityManager.persistData(userId);
    }
}

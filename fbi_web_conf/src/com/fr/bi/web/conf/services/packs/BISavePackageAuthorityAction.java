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
        String packageId = WebUtils.getHTTPRequestParameter(req, "packageId");
        String roles = WebUtils.getHTTPRequestParameter(req, "roles");
        long userId = ServiceUtils.getCurrentUserID(req);
        savePackageAuthority(packageId, roles, userId);
    }

    /**
     * 保存业务包权限
     *
     * @param packageId 业务包名字
     * @param roles  权限的字符串
     * @throws Exception
     */
    public void savePackageAuthority(String packageId, String roles, long userId) throws Exception {
        JSONArray roleInfojo=new JSONArray(roles);
        JSONArray packageIdjo=new JSONArray(packageId);

        String[] rolesArray=new String[roleInfojo.length()];
        for (int i = 0; i < roleInfojo.length(); i++) {
            rolesArray[i]= String.valueOf(roleInfojo.getString(i));
        }

        BISystemPackAndAuthConfigurationProvider packageAndAuthorityManager = BIConfigureManagerCenter.getPackageAndAuthorityManager();


        for (int i = 0; i < packageIdjo.length(); i++) {
            BIPackAndAuthority biPackAndAuthority=new BIPackAndAuthority();
            biPackAndAuthority.setBiPackageID( String.valueOf(packageIdjo.getString(i)));
            biPackAndAuthority.setRoleIdArray(rolesArray);


            boolean isExisted=packageAndAuthorityManager.containPackage(userId,biPackAndAuthority);
            if(isExisted){
            packageAndAuthorityManager.updateAuthority(userId,biPackAndAuthority);
            }else {
            packageAndAuthorityManager.addPackage(userId,biPackAndAuthority);
            }


        }


        packageAndAuthorityManager.persistData(userId);


    }
}

package com.fr.bi.web.conf.services.packs;

import com.fr.bi.conf.base.pack.data.BIPackAndAuthority;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BISystemPackAndAuthConfigurationProvider;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wuk on 16/4/26.
 */
public class BIGetPackageAuthorityAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String packageId = WebUtils.getHTTPRequestParameter(req, "packageId");
        BISystemPackAndAuthConfigurationProvider packageAndAuthorityManager = BIConfigureManagerCenter.getPackageAndAuthorityManager();

        BIPackAndAuthority biPackAndAuthority = packageAndAuthorityManager.getPackageByID(userId, packageId);
        JSONObject jo = new JSONObject().put("roles", biPackAndAuthority==null?"[]":biPackAndAuthority.getRoleIdArray());
        WebUtils.printAsJSON(res, jo);


    }



    @Override
    public String getCMD() {
        return "get_package_authority";
    }
}

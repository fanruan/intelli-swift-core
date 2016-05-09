package com.fr.bi.web.conf.services.packs;

import com.fr.bi.conf.base.pack.data.BIBasicBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BISystemPackAndAuthConfigurationProvider;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * Created by wuk on 16/4/26.
 */
public class BIGetPackageAuthorityAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        BISystemPackAndAuthConfigurationProvider packageAndAuthorityManager = BIConfigureManagerCenter.getPackageAndAuthorityManager();

        Set<BIBusinessPackage> allPackages = packageAndAuthorityManager.getAllPackages(userId);
        packageAndAuthorityManager.addPackage(userId, new BIBasicBusinessPackage(new BIPackageID("新建业务包c")));
        packageAndAuthorityManager.persistData(userId);
//        BIConfigureManagerCenter.getPackageAndAuthorityManager().persistData(userId);
//        JSONObject jo = new JSONObject().put("packages", packageAndAuthorityManager.createPackageJSON(userId));
//        WebUtils.printAsJSON(res, jo);

    }



    @Override
    public String getCMD() {
        return "get_package_authority";
    }
}

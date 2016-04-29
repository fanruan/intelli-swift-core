package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetPackageAuthorityAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_package_authority";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
//        String packageName = WebUtils.getHTTPRequestParameter(req, "packageName");
//        long userId = ServiceUtils.getCurrentUserID(req);
//        BIBusiPack pack = StableFactory.getMarkedObject(BIBusiPackManagerProvider.XML_TAG, BIBusiPackManagerProvider.class).getPackageManager(userId).getCurrent().getPackage(packageName);
//        JSONObject retJo = pack == null ? new JSONObject() : pack.createJSONWithAuthority();
//
//        WebUtils.printAsJSON(res, retJo);
    }

}
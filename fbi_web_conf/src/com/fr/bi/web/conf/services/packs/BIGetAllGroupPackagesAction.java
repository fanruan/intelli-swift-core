package com.fr.bi.web.conf.services.packs;

import com.fr.bi.stable.utils.conf.BISystemEnvUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetAllGroupPackagesAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_all_group_packages";
    }

    @Override
    public void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        if (BISystemEnvUtils.isSystemEnvProper()) {
            JSONObject jo = new JSONObject();
            jo.put("invalid", true);
            WebUtils.printAsJSON(res, jo);
        } else {
//            WebUtils.printAsJSON(res, StableFactory.getMarkedObject(BISystemPackageConfigProvider.XML_TAG, BISystemPackageConfigProvider.class).getCurrent(userId).createJSON());
        }
    }

}
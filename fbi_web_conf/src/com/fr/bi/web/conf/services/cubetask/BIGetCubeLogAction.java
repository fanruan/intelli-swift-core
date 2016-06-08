package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetCubeLogAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_cube_log";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {

        long userId = ServiceUtils.getCurrentUserID(req);
        WebUtils.printAsJSON(res, BIConfigureManagerCenter.getLogManager().createJSON(userId));
    }
}

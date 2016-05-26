/**
 *
 */
package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class BICheckCubeStatusAction extends AbstractBIConfigureAction {


    @Override
    public String getCMD() {

        return "check_cube_status";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        WebUtils.printAsJSON(res, new JSONObject().put("status", BICubeConfigureCenter.getCubeManager().checkCubeStatus(userId)));
    }

}
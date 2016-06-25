/**
 *
 */
package com.fr.bi.web.conf.services.cubetask;


import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.stable.constant.Status;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class BIGetCubeGenerateStatusAction extends AbstractBIConfigureAction {


    @Override
    public String getCMD() {

        return "get_cube_generate_status";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {

        long useId = ServiceUtils.getCurrentUserID(req);
        BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();
        JSONObject jo = new JSONObject();
        Status status = cubeManager.getStatus(useId);
        jo.put("loaded", status==Status.LOADED);
        WebUtils.printAsJSON(res, jo);
    }

}
package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.utils.BICubeLogJSONHelper;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zcf on 2017/2/28.
 */
public class BIGetCubeLogGenerateTimeInfoAction extends AbstractBIConfigureAction {

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject generateTime = new JSONObject();
        JSONObject cubeLogGenerateStartInfo = BICubeLogJSONHelper.getCubeLogGenerateStartJSON();
        JSONObject cubeLog = BIConfigureManagerCenter.getLogManager().createJSON(userId);
        Object process = cubeLog.get("process");
        generateTime.put("process", process);
        generateTime.put("generateStart", cubeLogGenerateStartInfo);
        generateTime.put("hasTask", CubeGenerationManager.getCubeManager().hasTask(userId));

        WebUtils.printAsJSON(res, generateTime);
    }

    @Override
    public String getCMD() {
        return "get_cube_log_generate_time_info";
    }
}
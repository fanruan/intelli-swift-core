package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.utils.code.BIPrintUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by kary on 2016/9/12.
 */
public class BITurnOffDeployModeAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        try {
            PerformancePlugManager.getInstance().setDeployModeSelectSize(PerformancePlugManager.DEFAULT_DEPLOY_MODE_OFF);
            WebUtils.printAsJSON(res, new JSONObject().put("message:",
                    "System has turned off deploy-mode." +
                            "However you must pay attention to the mode that " +
                            "current system is running in won't be persisted to the configure file." +
                            "So when you restart Fine BI next time, which mode the system running in will be decided by the " +
                            "value of parameter setting in configure file"));
        } catch (Exception e) {
            WebUtils.printAsJSON(res, new JSONObject().put("message:", BIPrintUtils.outputException(e)));
        }
    }

    @Override
    public String getCMD() {
        return "turn_off_deploy_mode";
    }


}

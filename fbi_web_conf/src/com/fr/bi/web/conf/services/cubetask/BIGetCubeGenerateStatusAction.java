/**
 *
 */
package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.utils.BIPackUtils;
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
        BICubeManagerProvider cubeManager = BIConfigureManagerCenter.getCubeManager();
        boolean hasCheck = false;
        boolean isChecking = false;
        boolean isAlling = false;
        boolean isSingleing = false;
        if (cubeManager.hasTask(useId)) {
            if (cubeManager.hasCheckTask(useId)) {
                isChecking = true;
            } else if (cubeManager.hasAllTask(useId)) {
                isAlling = true;
            } else {
                isSingleing = true;
            }
            if (!cubeManager.hasWaitingCheckTask(useId)) {
                hasCheck = BIPackUtils.getGeneratingChangeCounts(useId) > 0;
            }
        } else {
            hasCheck = BIPackUtils.getPackageChangeCounts(useId) > 0;
        }
        JSONObject jo = new JSONObject();
        jo.put("hasCheck", hasCheck);
        jo.put("isChecking", isChecking);
        jo.put("isAlling", isAlling);
        jo.put("isSingleing", isSingleing);
        WebUtils.printAsJSON(res,  jo);

    }

}
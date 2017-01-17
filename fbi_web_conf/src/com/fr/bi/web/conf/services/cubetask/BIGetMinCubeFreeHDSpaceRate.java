package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.utils.code.BIPrintUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by roy on 2017/1/17.
 */
public class BIGetMinCubeFreeHDSpaceRate extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        try {
            WebUtils.printAsJSON(res, new JSONObject().put("message:", "the MinCubeFreeHDSpaceRate is" + PerformancePlugManager.getInstance().getMinCubeFreeHDSpaceRate()));
        } catch (Exception e) {
            WebUtils.printAsJSON(res, new JSONObject().put("message:", BIPrintUtils.outputException(e)));
        }
    }

    @Override
    public String getCMD() {
        return "get_min_cube_free_hd_space_rate";
    }
}

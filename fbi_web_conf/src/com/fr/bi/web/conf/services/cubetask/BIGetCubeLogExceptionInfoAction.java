package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.utils.BICubeLogJSONHelper;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zcf on 2017/2/28.
 */
public class BIGetCubeLogExceptionInfoAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONObject exceptionInfo = BICubeLogJSONHelper.getCubeLogExceptionInfoJSON();

        WebUtils.printAsJSON(res, exceptionInfo);
    }


    @Override
    public String getCMD() {
        return "get_cube_log_exception_info";
    }
}
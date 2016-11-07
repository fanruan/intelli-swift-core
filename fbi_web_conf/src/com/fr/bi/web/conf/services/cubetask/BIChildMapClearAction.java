package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.CubeReaderCacheUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by kary on 2016/9/12.
 */
public class BIChildMapClearAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {

        WebUtils.printAsJSON(res, new JSONObject().put("result", "childMap_clear_success"));
    }

    @Override
    public String getCMD() {
        return "childMap_clear";
    }

    private void clearCache() {
         CubeReaderCacheUtils.clearCubeReader();
    }
}

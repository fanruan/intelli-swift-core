package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.CubeReaderCacheUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by kary on 2016/9/12.
 */
public class BIUserMapClearAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        WebUtils.printAsJSON(res, new JSONObject().put("result", "userMap_clear:"+clearCache()));
    }

    @Override
    public String getCMD() {
        return "userMap_clear";
    }

    private String clearCache() {
        BILogger.getLogger().info("start clear userMap");
        String readingCubeReader = CubeReaderCacheUtils.clearUserMap();
        BILogger.getLogger().info("userMap clear finished");
        return readingCubeReader;
    }
}

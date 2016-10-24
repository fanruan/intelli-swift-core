package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.CubeReaderCacheUtils;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by kary on 2016/9/12.
 */
public class BIUserMapCacheClearAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        WebUtils.printAsJSON(res, new JSONObject().put("result", "userMap_clear:"+clearCache()));
    }

    @Override
    public String getCMD() {
        return "userMapCache_clear";
    }

    private String clearCache() {
        BILoggerFactory.getLogger().info("start clearAnalysisETLCache userMap");
        String readingCubeReader = CubeReaderCacheUtils.clearUserMapCache();
        BILoggerFactory.getLogger().info("userMap clearAnalysisETLCache finished");
        return readingCubeReader;
    }
}

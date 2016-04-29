package com.fr.bi.web.base.fs;

import com.fr.base.ChartPreStyleServerManager;
import com.fr.base.FRContext;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2015/8/7.
 */
public class BIFSSetConfigAction extends AbstractBIBaseAction {

    @Override
    public String getCMD() {
        return "set_config_setting";
    }

    /**
     * 注释
     *
     * @param req 注释
     * @param res 注释
     * @return 注释
     */
    @Override
    public void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        FBIConfig.getInstance().getChartStyleAttr().setChartStyle(Integer.parseInt(WebUtils.getHTTPRequestParameter(req, "chartStyle")));
        FBIConfig.getInstance().getChartStyleAttr().setDefaultStyle(Integer.parseInt(WebUtils.getHTTPRequestParameter(req, "defaultStyle")));
        ChartPreStyleServerManager.getInstance().setCurrentStyle(WebUtils.getHTTPRequestParameter(req, "defaultColor"));

        FRContext.getCurrentEnv().writeResource(ChartPreStyleServerManager.getInstance());
        FRContext.getCurrentEnv().writeResource(FBIConfig.getInstance());
    }

}
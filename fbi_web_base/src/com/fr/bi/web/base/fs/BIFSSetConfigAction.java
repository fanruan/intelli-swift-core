package com.fr.bi.web.base.fs;

import com.fr.base.ChartPreStyleServerManager;
import com.fr.base.FRContext;
import com.fr.bi.conf.fs.BIChartStyleAttr;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.conf.fs.tablechartstyle.*;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.json.JSONObject;
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
        JSONObject mainBackground = new JSONObject(WebUtils.getHTTPRequestParameter(req, "mainBackground"));
        JSONObject widgetBackground = new JSONObject(WebUtils.getHTTPRequestParameter(req, "widgetBackground"));
        JSONObject titleBackground = new JSONObject(WebUtils.getHTTPRequestParameter(req, "titleBackground"));
        JSONObject titleFont = new JSONObject(WebUtils.getHTTPRequestParameter(req, "titleFont"));
        JSONObject chartFont = new JSONObject(WebUtils.getHTTPRequestParameter(req, "chartFont"));
        BIChartStyleAttr chartStyleAttr = FBIConfig.getInstance().getChartStyleAttr();
        chartStyleAttr.setChartStyle(Integer.parseInt(WebUtils.getHTTPRequestParameter(req, "chartStyle")));
        chartStyleAttr.setControlTheme(WebUtils.getHTTPRequestParameter(req, "controlTheme"));
        chartStyleAttr.setChartFont(new BIChartFontStyleAttr(chartFont.optString("color", ""),chartFont.optString("font-style", ""),chartFont.optString("font-widget", "")));
        chartStyleAttr.setTitleFont(new BITitleFontStyleAttr(titleFont.optString("color", ""),titleFont.optString("font-style", ""),titleFont.optString("font-widget", ""), titleFont.optString("text-align", "")));
        chartStyleAttr.setMainBackground(new BIMainBackgroundAttr(mainBackground.optString("value", ""), mainBackground.optInt("type", 1)));
        chartStyleAttr.setTitleBackground(new BITitleBackgroundAttr(titleBackground.optString("value", ""), titleBackground.optInt("type", 1)));
        chartStyleAttr.setWidgetBackground(new BIWidgetBackgroundAttr(widgetBackground.optString("value", ""), widgetBackground.optInt("type", 1)));
//        FBIConfig.getInstance().getChartStyleAttr().setDefaultStyle(Integer.parseInt(WebUtils.getHTTPRequestParameter(req, "defaultStyle")));
        ChartPreStyleServerManager.getInstance().setCurrentStyle(WebUtils.getHTTPRequestParameter(req, "defaultColor"));

        FRContext.getCurrentEnv().writeResource(ChartPreStyleServerManager.getInstance());
        FRContext.getCurrentEnv().writeResource(FBIConfig.getInstance());
    }
}
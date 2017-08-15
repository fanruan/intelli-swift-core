package com.fr.fs.mapeditor.server.service;

import com.fr.fs.mapeditor.server.MapLayerConfigManager;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by eason on 2017/7/20.
 */
public class MapEditorDeleteLayerEntryAction extends ActionNoSessionCMD {

    public String getCMD() {
        return "delete_layer_entry";
    }

    /**
     * 执行Action
     * @param req http请求
     * @param res http应答
     * @throws Exception 异常
     */
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {

        MapLayerConfigManager.getInstance().removeLayerEntry(WebUtils.getHTTPRequestParameter(req, "name"));

    }

}
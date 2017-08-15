package com.fr.fs.mapeditor.server.service;

import com.fr.json.JSONObject;
import com.fr.fs.mapeditor.server.MapLayerConfigManager;
import com.fr.fs.mapeditor.server.MapTileLayer;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by eason on 2017/7/20.
 */
public class MapEditorSaveLayerEntryAction extends ActionNoSessionCMD {

    public String getCMD() {
        return "save_layer_entry";
    }

    /**
     * 执行Action
     * @param req http请求
     * @param res http应答
     * @throws Exception 异常
     */
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {

        String name = WebUtils.getHTTPRequestParameter(req, "name");

        boolean isTileLayer = WebUtils.getHTTPRequestBoolParameter(req, "isTileLayer");

        String attribution = WebUtils.getHTTPRequestParameter(req, "attribution");

        String layerURL = WebUtils.getHTTPRequestParameter(req, "layerURL");

        JSONObject layers = new JSONObject(WebUtils.getHTTPRequestParameter(req, "layers"));
        HashMap<String, Boolean> wmsLayers = new HashMap<String, Boolean>();
        Iterator iterator = layers.keys();
        while(iterator.hasNext()){
            String key = iterator.next().toString();
            wmsLayers.put(key, layers.optBoolean(key));
        }

        MapTileLayer layer = MapLayerConfigManager.getInstance().getMapTileLayer(name);

        if(layer != null){
            layer.updateTileLayer(layerURL, attribution, wmsLayers, isTileLayer);

            MapLayerConfigManager.getInstance().writeResource();
        }

    }

}
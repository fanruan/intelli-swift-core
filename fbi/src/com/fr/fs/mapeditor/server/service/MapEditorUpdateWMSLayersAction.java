package com.fr.fs.mapeditor.server.service;

import com.fr.general.http.HttpClient;
import com.fr.fs.mapeditor.vanchart.map.designer.WMSFactory;
import com.fr.fs.mapeditor.vanchart.map.layer.WMSLayer;
import com.fr.fs.mapeditor.server.MapLayerConfigManager;
import com.fr.fs.mapeditor.server.MapTileLayer;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

public class MapEditorUpdateWMSLayersAction extends ActionNoSessionCMD {
    public String getCMD() {
        return "update_wms_layers";
    }

    /**
     * 执行Action
     * @param req http请求
     * @param res http应答
     * @throws Exception 异常
     */
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {

        String layerURL = WebUtils.getHTTPRequestParameter(req, "layerURL");

        String name = WebUtils.getHTTPRequestParameter(req, "name");

        HttpClient httpClient = new HttpClient(layerURL + "service=WMS&request=GetCapabilities");
        httpClient.asGet();

        if(httpClient.isServerAlive()){
            List<WMSLayer> list = WMSFactory.readLayers(httpClient.getResponseText());

            String[] layers = new String[list.size()];

            for(int i = 0, len = list.size(); i < len; i++){
                layers[i] = list.get(i).getLayer();
            }

            MapTileLayer tileLayer = MapLayerConfigManager.getInstance().getMapTileLayer(name);
            if(tileLayer != null){

                tileLayer.updateWmsLayers(layerURL, layers);

                MapLayerConfigManager.getInstance().writeResource();

                PrintWriter writer = WebUtils.createPrintWriter(res);
                writer.print(tileLayer.getEntryConfig(name).toString());
                writer.flush();
                writer.close();
            }
        }

    }
}

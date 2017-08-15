package com.fr.fs.mapeditor.server.service;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.fs.mapeditor.VanChartAttrHelper;
import com.fr.fs.mapeditor.geojson.JSONMapper;
import com.fr.fs.mapeditor.server.GEOJSONHelper;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by eason on 2017/6/30.
 */
public class MapEditorGetJSONAction extends ActionNoSessionCMD {

    public String getCMD() {
        return "get_json";
    }

    /**
     * 执行Action
     * @param req http请求
     * @param res http应答
     * @throws Exception 异常
     */
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String nodePath = WebUtils.getHTTPRequestParameter(req, "nodePath");

        String jsonURL = GEOJSONHelper.getInstance().getJsonUrlByPath(nodePath);

        JSONMapper area = GEOJSONHelper.getInstance().getAreaGeoJSON(jsonURL);
        JSONMapper point = GEOJSONHelper.getInstance().getPointGeoJSON(jsonURL);
        JSONMapper image = GEOJSONHelper.getInstance().getImageGeoJSON(jsonURL);

        JSONArray json = JSONArray.create();
        json.put(area == null ? null : new JSONObject(area.toString()));
        json.put(point == null ? null : new JSONObject(point.toString()));
        json.put(image == null ? null : new JSONObject(image.toString()));

        for(int i = 0, length = json.length(); i < length; i++){
            JSONObject map = json.optJSONObject(i);
            if(map != null && GEOJSONHelper.getInstance().isImageMap(jsonURL)){
                map.put("imageUrl", VanChartAttrHelper.getCustomImageURL(jsonURL, req.getSession().getId(), req));
            }
        }

        PrintWriter writer = WebUtils.createPrintWriter(res);
        writer.print(json.toString());
        writer.flush();
        writer.close();
    }
}

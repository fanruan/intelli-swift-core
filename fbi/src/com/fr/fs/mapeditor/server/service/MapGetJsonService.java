package com.fr.fs.mapeditor.server.service;

import com.fr.plugin.chart.map.geojson.JSONMapper;
import com.fr.plugin.chart.map.server.CompatibleGEOJSONHelper;
import com.fr.stable.CodeUtils;
import com.fr.stable.fun.Service;
import com.fr.web.utils.WebUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MapGetJsonService implements Service {
    public MapGetJsonService() {
    }

    public String actionOP() {
        return "get_json_data";
    }

    public void process(HttpServletRequest var1, HttpServletResponse var2, String var3, String var4) throws Exception {
        String var5 = CodeUtils.cjkDecode(WebUtils.getHTTPRequestParameter(var1, "resourcepath"));
        JSONMapper var6 = CompatibleGEOJSONHelper.getGeoJSON(var5);
        if (var6 != null) {
            WebUtils.printAsString(var2, var6.toString());
        }

    }
}
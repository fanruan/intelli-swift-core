package com.fr.fs.mapeditor;

import com.fr.base.FRContext;
import com.fr.base.chart.ChartWebSource;
import com.fr.fs.mapeditor.geojson.JSONMapper;
import com.fr.fs.mapeditor.server.CompatibleGEOJSONHelper;
import com.fr.json.JSONException;
import com.fr.stable.StringUtils;

import javax.servlet.http.HttpServletRequest;


public class VanChartAttrHelper {
    public static String getCustomImageURL(String geourl, String sesstionID, HttpServletRequest req) throws JSONException {
        try {
            JSONMapper jsonMapper = CompatibleGEOJSONHelper.getGeoJSON(geourl);

            String embID = jsonMapper.getEmbID();
            ChartWebSource.putAttachment(sesstionID, embID);

            return req.getRequestURI() + "?op=fr_attach&cmd=ah_image&id=" + embID;
        } catch (Exception e) {
            FRContext.getLogger().info(e.getMessage());
            return StringUtils.EMPTY;
        }
    }
}

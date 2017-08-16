package com.fr.fs.mapeditor.server;

import com.fr.fs.mapeditor.geojson.JSONMapper;
import com.fr.stable.StringUtils;


public class CompatibleGEOJSONHelper {
    public CompatibleGEOJSONHelper() {
    }

    public static boolean isDeprecated(String var0) {
        return StringUtils.contains(var0, "geojson");
    }

    public static JSONMapper getGeoJSON(String var0) {
        JSONMapper var10000;
        if (isDeprecated(var0)) {
            DeprecatedGEOJSONHelper.getInstance();
            var10000 = DeprecatedGEOJSONHelper.getGeoJSON(var0);
        } else {
            GEOJSONHelper.getInstance();
            var10000 = GEOJSONHelper.getGeoJSON(var0);
        }

        return var10000;
    }

    public static JSONMapper getPointGeoJSON(String var0) {
        JSONMapper var10000;
        if (isDeprecated(var0)) {
            DeprecatedGEOJSONHelper.getInstance();
            var10000 = DeprecatedGEOJSONHelper.getGeoJSON(var0);
        } else {
            GEOJSONHelper.getInstance();
            var10000 = GEOJSONHelper.getPointGeoJSON(var0);
        }

        return var10000;
    }

    public static JSONMapper getAreaGeoJSON(String var0) {
        JSONMapper var10000;
        if (isDeprecated(var0)) {
            DeprecatedGEOJSONHelper.getInstance();
            var10000 = DeprecatedGEOJSONHelper.getGeoJSON(var0);
        } else {
            GEOJSONHelper.getInstance();
            var10000 = GEOJSONHelper.getAreaGeoJSON(var0);
        }

        return var10000;
    }

    public static boolean isParamURL(String var0) {
        boolean var1 = isDeprecated(var0);
        String var2 = var1 ? DeprecatedGEOJSONHelper.getMapDataTypePath(MapDataType.PARAM) : GEOJSONHelper.getMapDataTypePath(MapDataType.PARAM);
        return var0.contains(var2);
    }
}

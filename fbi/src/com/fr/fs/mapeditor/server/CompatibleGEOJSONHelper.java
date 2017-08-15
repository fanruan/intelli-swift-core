package com.fr.fs.mapeditor.server;

import com.fr.plugin.chart.drillmap.VanChartDrillMapDataPoint;
import com.fr.fs.mapeditor.geojson.JSONMapper;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CompatibleGEOJSONHelper {
    public static final String ROOT_MARK = "rootgeojsonroot";
    public static final String GEO_JSON_SUFFIX = ".json";

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

    public static Map<String, VanChartDrillMapDataPoint> getMapRootNodeAndAllLeafs(String var0) {
        JSONMapper var1 = getGeoJSON(var0);
        VanChartDrillMapDataPoint var2 = new VanChartDrillMapDataPoint();
        var2.setValid();
        HashMap var3 = new HashMap();
        var3.put("rootgeojsonroot", var2);
        if (var1 == null) {
            return var3;
        } else {
            initMapChildNode(var1, var2, 1, var3, (Map)null);
            return var3;
        }
    }

    private static void initMapChildNode(JSONMapper var0, VanChartDrillMapDataPoint var1, int var2, Map<String, VanChartDrillMapDataPoint> var3, Map<Integer, Map<String, VanChartDrillMapDataPoint>> var4) {
        if (var0 != null && var1 != null) {
            Set var5 = var0.calculateAreaNames();
            String var6 = var0.getPath().replace(".json", "");
            var6 = GEOJSONHelper.getPartialJSONURL(var6);
            boolean var7 = true;
            Iterator var8 = var5.iterator();

            String var9;
            String var10;
            while(var8.hasNext()) {
                var9 = (String)var8.next();
                var10 = StableUtils.pathJoin(new String[]{var6, var9 + ".json"});
                if (getGeoJSON(var10) != null) {
                    var7 = false;
                    break;
                }
            }

            var8 = var5.iterator();

            while(var8.hasNext()) {
                var9 = (String)var8.next();
                var10 = StableUtils.pathJoin(new String[]{var6, var9 + ".json"});
                VanChartDrillMapDataPoint var11 = new VanChartDrillMapDataPoint();
                var11.setValueIsNull(true);
                var11.setLevel(var2);
                var11.setMapAreaName(var9);
                var11.setParent(var1);
                var1.addChildren(var11);
                if (var3 != null && var7) {
                    var3.put(var9, var11);
                }

                if (var4 != null) {
                    addNodeToLevelMap(var9, var11, var4);
                }

                JSONMapper var12 = getGeoJSON(var10);
                if (var12 != null) {
                    var11.setGeoUrl(var10);
                    initMapChildNode(var12, var11, var2 + 1, var3, var4);
                }
            }

        }
    }

    public static Map<Integer, Map<String, VanChartDrillMapDataPoint>> getMapRootNodeAndAllLevelNodes(String var0) {
        JSONMapper var1 = getGeoJSON(var0);
        VanChartDrillMapDataPoint var2 = new VanChartDrillMapDataPoint();
        var2.setValid();
        HashMap var3 = new HashMap();
        addNodeToLevelMap("rootgeojsonroot", var2, var3);
        if (var1 == null) {
            return var3;
        } else {
            initMapChildNode(var1, var2, 1, (Map)null, var3);
            return var3;
        }
    }

    private static void addNodeToLevelMap(String var0, VanChartDrillMapDataPoint var1, Map<Integer, Map<String, VanChartDrillMapDataPoint>> var2) {
        int var3 = var1.getLevel();
        Object var4 = (Map)var2.get(var3);
        if (var4 == null) {
            var4 = new HashMap();
            var2.put(var3, (Map<String, VanChartDrillMapDataPoint>) var4);
        }

        ((Map)var4).put(var0, var1);
    }
}

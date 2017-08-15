package com.fr.fs.mapeditor.server;

import com.fr.base.FRContext;
import com.fr.file.filetree.FileNode;
import com.fr.general.FRLogger;
import com.fr.general.IOUtils;
import com.fr.fs.mapeditor.geojson.GeoJSONFactory;
import com.fr.fs.mapeditor.geojson.JSONMapper;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;
import java.io.InputStream;
import java.util.HashMap;

public class DeprecatedGEOJSONHelper {
    public static final String GEO_JSON_DIR_NAME = "geojson";
    public static final String POINT_ROOT_NAME = "point";
    public static final String AREA_ROOT_NAME = "area";
    public static final String IMAGE_ROOT_NAME = "image";
    public static final String GEO_JSON_SUFFIX = ".json";
    private static HashMap<String, JSONMapper> geoJSONHashMap = new HashMap();
    private static DeprecatedGEOJSONHelper helperInstance = null;

    public DeprecatedGEOJSONHelper() {
    }

    public static synchronized DeprecatedGEOJSONHelper getInstance() {
        if (helperInstance == null) {
            try {
                helperInstance = new DeprecatedGEOJSONHelper();
                readJSON(FRContext.getCurrentEnv().listFile(StableUtils.pathJoin(new String[]{"resources", "geojson", "area"})));
                readJSON(FRContext.getCurrentEnv().listFile(StableUtils.pathJoin(new String[]{"resources", "geojson", "point"})));
                readJSON(FRContext.getCurrentEnv().listFile(StableUtils.pathJoin(new String[]{"resources", "geojson", "image"})));
            } catch (Exception var1) {
                FRLogger.getLogger().error(var1.getMessage());
            }
        }

        return helperInstance;
    }

    public static String getMapDataTypePath(MapDataType var0) {
        String var1 = var0.getStringType();
        return StableUtils.pathJoin(new String[]{"resources", "geojson", var1});
    }

    private static void readJSON(FileNode[] var0) throws Exception {
        FileNode[] var1 = var0;
        int var2 = var0.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            FileNode var4 = var1[var3];
            String var5 = var4.getEnvPath();
            if (var5.endsWith(".json")) {
                geoJSONHashMap.put(var5, getSingleGeoJSON(var5));
            }

            if (var4.isDirectory()) {
                readJSON(FRContext.getCurrentEnv().listFile(var5));
            }
        }

    }

    public static JSONMapper getSingleGeoJSON(String var0) {
        String var1 = "";

        try {
            InputStream var2 = FRContext.getCurrentEnv().readBean(var0, "");
            if (var2 != null) {
                var1 = IOUtils.inputStream2String(var2);
            }
        } catch (Exception var3) {
            FRContext.getLogger().error(var3.getMessage(), var3);
        }

        if (StringUtils.isNotEmpty(var1)) {
            var1 = var1.replace("\ufeff", "");

            try {
                JSONMapper var5 = GeoJSONFactory.create(var1, var0);
                return var5;
            } catch (Exception var4) {
                FRContext.getLogger().error("error to read  " + var0 + "\n" + var4.getMessage());
            }
        }

        return null;
    }

    public static JSONMapper getGeoJSON(String var0) {
        return (JSONMapper)geoJSONHashMap.get(getGeoJSONFileName(var0));
    }

    public static String getGeoJSONFileName(String var0) {
        return var0.contains(".json") ? var0 : var0 + ".json";
    }
}

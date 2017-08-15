package com.fr.fs.mapeditor.server;

import com.fr.general.ComparatorUtils;

public enum MapDataType {
    GEOGRAPHIC("geographic"),
    IMAGE("image"),
    PARAM("param"),
    AREA("area"),
    POINT("point");

    private String stringType;
    private static MapDataType[] types;

    private MapDataType(String var3) {
        this.stringType = var3;
    }

    public String getStringType() {
        return this.stringType;
    }

    public static boolean isRootMapData(String var0) {
        return parse(var0) != null;
    }

    public static MapDataType parse(String var0) {
        if (types == null) {
            types = values();
        }

        MapDataType[] var1 = types;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            MapDataType var4 = var1[var3];
            if (ComparatorUtils.equals(var4.getStringType(), var0)) {
                return var4;
            }
        }

        return null;
    }
}

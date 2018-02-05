package com.fr.swift.cube.io.location;

/**
 * @author yee
 * @date 2017/8/3
 */
public final class LocationUtil {
    private LocationUtil() {
    }

//    public static URI toKeyUri(ICubeResourceLocation location) {
//        String path = location.getAbsolutePath();
//        return toKeyUri(path);
//    }

    public static String unifySlash(String before) {
        // 不同系统路径分割符不一样，统一下
        return before.replace('\\', '/');
    }

//    public static URI toKeyUri(String path) {
//        String p = unifySlash(path);
//        // fixme
//        String base = unifySlash("/cubes/"/*BIConfigurePathUtils.createBasePath()*/);
//        int index = p.indexOf(base);
//        String key = p.substring(index + base.length());
//        return URI.create(BIStringUtils.cutStartSlash(key));
//    }
}

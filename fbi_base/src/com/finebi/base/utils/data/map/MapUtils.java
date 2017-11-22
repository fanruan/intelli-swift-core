package com.finebi.base.utils.data.map;

import java.util.Map;

/**
 * Created by andrew_asa on 2017/10/9.
 */
public class MapUtils {

    public static boolean isEmptyMap(Map map) {

        return map == null || map.isEmpty();
    }

    public static boolean isNotEmptyMap(Map map) {

        return map != null && !map.isEmpty();
    }
}

package com.fr.bi.stable.utils;

import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.TargetCalculator;

import java.util.*;

/**
 * Created by 小灰灰 on 2015/10/16.
 */
public class BIMapUtils {

    public static Map mergeMapByKeyMapValue(Map key, Map value) {
        Map merge = new HashMap();
        if (key == null) {
            return value;
        }
        Iterator<Map.Entry> it = key.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            Object ob;
            if (entry.getValue() instanceof TargetCalculator) {
                ob = value.get(((TargetCalculator) entry.getValue()).createTargetGettingKey());
            } else {
                ob = value.get(entry.getValue());
            }

            if (ob != null) {
                merge.put(entry.getKey(), ob);
            }
        }
        return merge;
    }

    /**
     * 从src合并到dest map
     *
     * @param dest
     * @param src
     * @param <T>
     * @param <V>
     */
    public static <T extends Object, V extends Set> void mergeSetValueMap(Map<T, V> dest, Map<T, V> src) {
        Iterator<Map.Entry<T, V>> it = src.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<T, V> entry = it.next();
            V set = dest.get(entry.getKey());
            if (set != null) {
                set.addAll(entry.getValue());
            } else {
                dest.put(entry.getKey(), entry.getValue());
            }
        }
    }

}
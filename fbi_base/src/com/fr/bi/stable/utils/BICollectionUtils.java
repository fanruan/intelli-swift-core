package com.fr.bi.stable.utils;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.stable.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/10/16.
 */
public class BICollectionUtils {

    public static Map createMapByKeyMapValue(Map<String, TargetCalculator> key, Number[] value) {
        Map merge = new HashMap();
        if (key == null) {
            return merge;
        }
        if (value == null) {
            return new HashMap();
        }
        Iterator<Map.Entry<String, TargetCalculator>> it = key.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, TargetCalculator> entry = it.next();
            Object ob = value[entry.getValue().createTargetGettingKey().getTargetIndex()];
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

    public static <T> T firstUnNullKey(ICubeColumnIndexReader<T> baseMap) {
        T firstKey = baseMap.firstKey();
        if (isCubeNullKey(firstKey)) {
            Iterator<Map.Entry<T, GroupValueIndex>> iter = baseMap.iterator(firstKey);
            while (iter.hasNext()) {
                Map.Entry<T, GroupValueIndex> entry = iter.next();
                firstKey = entry.getKey();
                if (isNotCubeNullKey(firstKey)) {
                    break;
                }
            }
        }
        return firstKey;
    }

    public static <T> T lastUnNullKey(ICubeColumnIndexReader<T> baseMap) {
        T lastKey = baseMap.lastKey();
        if (isCubeNullKey(lastKey)) {
            Iterator<Map.Entry<T, GroupValueIndex>> iter = baseMap.previousIterator(lastKey);
            while (iter.hasNext()) {
                Map.Entry<T, GroupValueIndex> entry = iter.next();
                lastKey = entry.getKey();
                if (isNotCubeNullKey(lastKey)) {
                    break;
                }
            }
        }
        return lastKey;
    }

    /**
     * 是否为cube底层返回空值
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> Boolean isCubeNullKey(T key) {
        // FIXME 来一个更好的判断方法。。。
        if (key == null) {
            return true;
        } else if (key instanceof Integer && NIOConstant.INTEGER.NULL_VALUE == (Integer) key) {
            return true;
        } else if (key instanceof String && StringUtils.EMPTY.equals(key)) {
            return true;
        } else if (key instanceof Double && NIOConstant.DOUBLE.NULL_VALUE == (Double)key) {
            return true;
        } else if (key instanceof Long && NIOConstant.LONG.NULL_VALUE == (Long) key) {
            return true;
        }
        return false;
    }

    /**
     * 是否为空值
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> boolean isNotCubeNullKey(T key) {
        return !isCubeNullKey(key);
    }

    /**
     * 底层的值显示给前端展示的值转换
     * 主要是处理空值的情况
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T cubeValueToWebDisplay(T key) {
        if (key == null || key instanceof String) {
            return key;
        }
        if (key instanceof Double && NIOConstant.DOUBLE.NULL_VALUE == ((Double) key)) {
            return null;
        }
        if (key instanceof Long && NIOConstant.LONG.NULL_VALUE == (Long) key) {
            return null;
        }
        if (key instanceof Integer && NIOConstant.INTEGER.NULL_VALUE == (Integer) key) {
            return null;
        }
        return key;
    }

}
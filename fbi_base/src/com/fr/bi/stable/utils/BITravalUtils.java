package com.fr.bi.stable.utils;

import com.fr.base.FRContext;
import com.fr.bi.base.provider.NameProvider;
import com.fr.general.ComparatorUtils;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 所有和遍历有关的基础方法
 * Created by GUY on 2015/4/20.
 */
public class BITravalUtils {

    /**
     * 从一系列targets值中找到名字是name的那一个
     *
     * @param name
     * @param targets
     * @param <T>
     * @return
     */
    public static <T extends NameProvider> T getTargetByName(String name, T[] targets) {
        for (int i = 0, len = targets.length; i < len; i++) {
            NameProvider t = targets[i];
            if (ComparatorUtils.equals(t.getValue(), name)) {
                return targets[i];
            }
        }
        FRContext.getLogger().error("error!target or dimension not found:" + name);
        return null;
    }

    public static <T extends NameProvider> T[] getTargetsOrDimensionsByName(Class c, HashSet<String> targetNames, NameProvider[] targets) {
        T[] summary = (T[])Array.newInstance(c, targetNames.size());
        Iterator<String> it = targetNames.iterator();
        int i = 0;
        while (it.hasNext()){
            summary[i] = (T) BITravalUtils.getTargetByName(it.next(), targets);
            i ++;
        }
        return summary;
    }
}
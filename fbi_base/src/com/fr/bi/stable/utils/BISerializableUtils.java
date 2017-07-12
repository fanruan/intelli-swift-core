package com.fr.bi.stable.utils;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.program.BIBeanUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.general.ComparatorUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * 检查class的序列化情况。
 * 检查目标：class自身，class的属性，super三部分。
 * <p/>
 * 1.如果class是序列化的话，那么子类都是序列化的。
 * 2.但是如果super没有序列化，但是子类是序列化。会导致
 * 父类部分的内容没有序列化。
 * This class created on 2016/11/23.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BISerializableUtils {


    public static Set<Class> findUnsupportedSerializable(Class target, String[] packageLimit) {
        return findUnsupportedSerializable(target, packageLimit, new HashSet<Class>());
    }

    public static Set<Class> findUnsupportedSerializable(Class target) {
        return findUnsupportedSerializable(target, new String[]{});
    }

    private static Set<Class> findUnsupportedSerializable(Class target, String[] packageLimit, Set<Class> filters) {
        BINonValueUtils.checkNull(target, packageLimit, filters);
        Set<Class> unsupportedClass = new HashSet<Class>();
        if (filters.contains(target)) {
            return unsupportedClass;
        } else {
            filters.add(target);
        }
        if (!isSerializable(target)) {
            unsupportedClass.add(target);
        } else {
            checkSerializableID(target);
        }
        unsupportedClass.addAll(scanSelfFields(target, packageLimit, filters));
        if (hasSuper(target)) {
            unsupportedClass.addAll(scanSuper(target.getSuperclass(), packageLimit, filters));
        }
        return unsupportedClass;
    }

    /**
     * 有没有非Object的父类
     *
     * @param target
     * @return
     */
    protected static boolean hasSuper(Class target) {
        return !Modifier.isInterface(target.getModifiers()) && !ComparatorUtils.equals(Object.class, target.getSuperclass());
    }

    private static void checkSerializableID(Class clazz) {
        for (Field field : BIBeanUtils.fetchAttributes(clazz)) {
            if (ComparatorUtils.equals(field.getName(), "serialVersionUID")) {
                return;
            }
        }
        BILoggerFactory.getLogger(BISerializableUtils.class).warn("The class:" + clazz.getName() + " needs serialVersionUID attr");
    }

    private static Set<Class> scanSuper(Class superClass, String[] packageLimit, Set<Class> filters) {
        return findUnsupportedSerializable(superClass, packageLimit, filters);
    }

    private static Set<Class> scanSelfFields(Class target, String[] packageLimit, Set<Class> filters) {
        Set<Class> unsupportedClass = new HashSet<Class>();
        for (Field field : BIBeanUtils.fetchAttributes(target)) {
            Class fieldClass = field.getType();
            if (!BITypeUtils.isBasicValue(fieldClass)) {
                if (!isSerializable(fieldClass)) {
                    unsupportedClass.add(fieldClass);
                }
                if (!reachLimit(fieldClass, packageLimit)) {
                    unsupportedClass.addAll(findUnsupportedSerializable(fieldClass, packageLimit, filters));
                }
            }
        }
        return unsupportedClass;
    }

    private static boolean isSerializable(Class clazz) {
        return BITypeUtils.isAssignable(Serializable.class, clazz);
    }

    private static boolean reachLimit(Class fieldClass, String[] packageLimit) {
        for (String limit : packageLimit) {
            if (checkClassPackage(fieldClass, limit)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkClassPackage(Class clazz, String packagePrefix) {
        BINonValueUtils.checkNull(clazz, packagePrefix);
        return clazz.getName().startsWith(packagePrefix);
    }
}

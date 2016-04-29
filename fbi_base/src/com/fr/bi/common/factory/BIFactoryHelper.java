package com.fr.bi.common.factory;

import com.fr.bi.stable.utils.code.BILogDelegate;


/**
 * 约定用class的名称作为key
 * 注册的key分为两种情况。
 * 一种是key对应当前的class的名称.提供数值
 * 另外一种是key为当前class的父类或者接口的名称。
 * 提供的是逻辑。
 * Created by Connery on 2015/12/8.
 */
public class BIFactoryHelper {

    /**
     * 获得该Class的注册的实例。
     *
     * @param clazz      注册的class
     * @param parameters 构造时候的参数
     * @param <T>
     * @return
     */
    public static <T> T getObject(Class<T> clazz, Object... parameters) {
        try {
            return (T) BIFactory.getInstance().getObject(clazz.getName(), parameters);
        } catch (Exception ex) {
            BILogDelegate.errorDelegate("", ex);
        }
        return (T) new Object();
    }

    public static <T> T getObject(Class<T> clazz) {
        try {
            return (T) BIFactory.getInstance().getObject(clazz.getName());
        } catch (Exception ex) {
            BILogDelegate.errorDelegate(ex.getMessage(), ex);
        }
        return (T) new Object();
    }

}
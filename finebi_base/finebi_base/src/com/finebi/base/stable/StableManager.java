package com.finebi.base.stable;

import com.finebi.base.context.BaseContext;

/**
 * This class created on 2017/12/21
 *
 * @author Each.Zhang
 */
public class StableManager {

    static BaseContext context;

    private static String[] scanPath = {
            "com.finebi.conf.aop",
            "com.finebi.conf.internalimp",
            "com.fr.swift.adaptor.model",
    };


    static {
        context = new BaseContext();
        context.addScanPath(scanPath);
        //registerEngineService();
    }

    public static BaseContext getContext() {

        return context;
    }

    //public static void registerEngineService() {
    //
    //    try {
    //        context.registerBeanDefinition("cubeManager", new RootBeanDefinition(getClass("com.finebi.conf.constant.CubeManager")));
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //}
    //
    //public static Class getClass(String clazz) throws ClassNotFoundException {
    //
    //    return BaseContext.class.getClassLoader().loadClass(clazz);
    //}

}

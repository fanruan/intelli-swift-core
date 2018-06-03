package com.fr.swift.test;

import com.fr.swift.context.SwiftContext;

/**
 * @author anchore
 * @date 2018/5/8
 */
public class Preparer {
    public static void preparePath() {
//        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir")));
    }

    public static void prepareCubeBuild() {
        prepareContext();
        preparePath();
    }

    public static void prepareContext() {
        SwiftContext.init();
    }
}
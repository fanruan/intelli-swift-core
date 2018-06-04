package com.fr.swift.test;

import com.fr.base.FRContext;
import com.fr.dav.LocalEnv;
import com.fr.swift.context.SwiftContext;

/**
 * @author anchore
 * @date 2018/5/8
 */
public class Preparer {
    public static void preparePath() {
        FRContext.setCurrentEnv(new LocalEnv());
    }

    public static void prepareCubeBuild() {
        prepareContext();
        preparePath();
    }

    public static void prepareContext() {
        SwiftContext.init();
    }
}
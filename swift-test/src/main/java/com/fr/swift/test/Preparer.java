package com.fr.swift.test;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.util.FileUtil;
import com.fr.workspace.simple.SimpleWork;

/**
 * @author anchore
 * @date 2018/5/8
 */
public class Preparer {
    public static void prepareFrEnv() {
        SimpleWork.checkIn(TestResource.getTmpDir());
    }

    public static void prepareCubeBuild() {
        prepareFrEnv();
        prepareContext();
        prepareConfDb();
        TestConnectionProvider.createConnection();
    }

    public static void prepareContext() {
        SwiftContext.init();
    }

    public static void prepareConfDb() {
        FileUtil.delete(TestResource.getTmpDir() + "/embed");
    }
}
package com.fr.swift.test;

import com.fr.swift.config.TestConfDb;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.workspace.simple.SimpleWork;

/**
 * @author anchore
 * @date 2018/5/8
 */
public class Preparer {
    public static void prepareFrEnv() {
        SimpleWork.checkIn(System.getProperty("user.dir"));
    }

    public static void prepareCubeBuild() throws Exception {
        prepareFrEnv();
        prepareContext();
        TestConfDb.setConfDb();
        TestConnectionProvider.createConnection();
    }

    public static void prepareContext() {
        SwiftContext.init();
    }
}
package com.fr.swift.test;

import com.fr.base.FRContext;
import com.fr.core.env.impl.LocalEnvConfig;
import com.fr.dav.LocalEnv;
import com.fr.swift.config.TestConfDb;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.source.db.TestConnectionProvider;

/**
 * @author anchore
 * @date 2018/5/8
 */
public class Preparer {
    public static void prepareFrEnv() {
        FRContext.setCurrentEnv(new LocalEnv(new LocalEnvConfig(System.getProperty("user.dir"))));
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
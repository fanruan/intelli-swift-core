package com.fr.swift.test;

import com.fr.base.FRContext;
import com.fr.core.env.EnvContext;
import com.fr.core.env.resource.LocalEnvConfig;
import com.fr.dav.LocalEnv;
import com.fr.startup.EnvInitializer;
import com.fr.swift.config.TestConfDb;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.source.db.TestConnectionProvider;

import java.io.File;

/**
 * @author anchore
 * @date 2018/5/8
 */
public class Preparer {
    public static void prepareFrEnv() {
        new EnvInitializer().start();
        File f = new File(System.getProperty("user.dir") + "/WEB-INF");
        f.mkdirs();
        EnvContext.signIn(new LocalEnvConfig(f.getAbsolutePath()));
        FRContext.setCurrentEnv(new LocalEnv());
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
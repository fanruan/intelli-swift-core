package com.fr.swift.test;

import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLog4jLoggers;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.util.FileUtil;
import com.fr.workspace.simple.SimpleWork;

/**
 * @author anchore
 * @date 2018/5/8
 */
public class Preparer {
    public static void prepareLogger() {
        SwiftLoggers.setLoggerFactory(new SwiftLog4jLoggers());
    }

    public static void prepareFrEnv() {
        SimpleWork.checkIn(TestResource.getTmpDir());
    }

    public static void prepareCubeBuild() {
        prepareFrEnv();
        prepareContext();
        prepareConfDb();
        TestConnectionProvider.createConnection();
    }

    public static void prepareCubeBuild(Class<?> test) {
        prepareFrEnv();
        prepareContext();
        prepareConfDb();
        beforeEachCubeBuild(test);
        TestConnectionProvider.createConnection();
    }


    public static void beforeEachCubeBuild(Class<?> test) {
        prepareConfDb();
        String runPath = TestResource.getRunPath(test);
        SwiftContext.get().getBean(SwiftCubePathService.class).setSwiftPath(runPath);
        FileUtil.delete(runPath);
    }

    public static void prepareContext() {
        prepareLogger();
        SwiftContext.init();
    }

    public static void prepareConfDb() {
        FileUtil.delete(TestResource.getTmpDir() + "/embed");
    }
}
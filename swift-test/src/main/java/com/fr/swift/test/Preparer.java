package com.fr.swift.test;

import com.fr.config.dao.DaoContext;
import com.fr.config.dao.impl.LocalClassHelperDao;
import com.fr.config.dao.impl.LocalEntityDao;
import com.fr.config.dao.impl.LocalXmlEntityDao;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLog4jLoggers;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.util.FileUtil;
import com.fr.workspace.WorkContext;
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

        String runPath = TestResource.getRunPath(test);
        FileUtil.delete(runPath);
        SwiftContext.get().getBean(SwiftCubePathService.class).setSwiftPath(runPath);

        TestConnectionProvider.createConnection();
    }

    public static void prepareContext() {
        prepareLogger();
        SwiftContext.init();
    }

    public static void prepareConfDb() {
        FileUtil.delete(WorkContext.getCurrent().getPath() + "/embed");

        DaoContext.setEntityDao(new LocalEntityDao());
        DaoContext.setClassHelperDao(new LocalClassHelperDao());
        DaoContext.setXmlEntityDao(new LocalXmlEntityDao());
    }
}
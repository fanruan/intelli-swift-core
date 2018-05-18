package com.fr.swift.log;

import com.fr.base.Env;
import com.fr.base.FRContext;
import com.fr.third.apache.log4j.Level;
import com.fr.third.apache.log4j.Logger;
import com.fr.third.apache.log4j.PropertyConfigurator;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author anchore
 */
public final class SwiftLoggers {
    private static final Map<Class, SwiftLogger> LOGGERS = new HashMap<Class, SwiftLogger>();
    private static final String FINE_LOG_PROPS = "FineLog.properties";

    private static final SwiftLogger ROOT_LOGGER = new SwiftLogger(Logger.getRootLogger());

    static {
        conf();
    }

    private static void conf() {
        Env env = FRContext.getCurrentEnv();
        // 这边单独测试，没运行bi，暂时先用default
//        if (env == null) {
            PropertyConfigurator.configure(new ByteArrayInputStream(DEFAULT_PROPS.getBytes()));
//            return;
//        }
//        String webInfPath = env.getPath();
//        String propsPath = webInfPath + "/lib/" + FINE_LOG_PROPS;
//        File f = new File(propsPath);
//        try {
//            PropertyConfigurator.configure(new FileInputStream(f));
//        } catch (FileNotFoundException e) {
//            FRLogger.getLogger().error(e.getMessage(), e);
//            PropertyConfigurator.configure(new ByteArrayInputStream(DEFAULT_PROPS.getBytes()));
//        }
    }

    public static SwiftLogger getLogger() {
        return ROOT_LOGGER;
    }

    public static SwiftLogger getLogger(Class cls) {
        if (LOGGERS.containsKey(cls)) {
            return LOGGERS.get(cls);
        }
        synchronized (LOGGERS) {
            if (LOGGERS.containsKey(cls)) {
                return LOGGERS.get(cls);
            }
            Logger logger = Logger.getLogger(cls);
            logger.setLevel(Level.INFO);
            SwiftLogger sl = new SwiftLogger(logger);
            LOGGERS.put(cls, sl);
            return sl;
        }
    }

    private static final String DEFAULT_PROPS =
            "log4j.rootLogger=INFO, stdout, ServerDailyRollingFile\n" +
                    "log4j.appender.ServerDailyRollingFile=com.fr.third.apache.log4j.DailyRollingFileAppender\n" +
                    "log4j.appender.ServerDailyRollingFile.File=logs/swift.log\n" +
                    "log4j.appender.ServerDailyRollingFile.DatePattern='.'yy-MM-dd\n" +
                    "log4j.appender.ServerDailyRollingFile.layout=com.fr.third.apache.log4j.PatternLayout\n" +
                    "log4j.appender.ServerDailyRollingFile.layout.ConversionPattern=%d{yy-MM-dd HH:mm:ss.S} %t %p [%C.%M] %m%n\n" +
                    "log4j.appender.ServerDailyRollingFile.Append=true\n" +
                    "log4j.appender.stdout=com.fr.third.apache.log4j.ConsoleAppender\n" +
                    "log4j.appender.stdout.layout=com.fr.third.apache.log4j.PatternLayout\n" +
                    "log4j.appender.stdout.layout.ConversionPattern=%d{yy-MM-dd HH:mm:ss.S} %t %p [%C.%M] %m%n";

}

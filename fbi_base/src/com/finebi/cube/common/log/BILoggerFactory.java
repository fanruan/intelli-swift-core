package com.finebi.cube.common.log;

import com.fr.base.FRContext;
import com.fr.bi.stable.utils.code.BIPrintUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.FRLogger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BI日志输出
 */
public class BILoggerFactory {

    private static final String LOG_PROPERTIES_URL = "FineLog.properties";
    private static final String LIB_FOLDER = "lib";
    private static final String LOAD_RESOURCE_NAME = "log4j.properties";

    static {
        /**
         * 添加默认Log配置
         */

//        if (PerformancePlugManager.getInstance().useLog4JPropertiesFile()) {
        File logFile = getLibLogFile();
        if (logFile != null) {
            try {
                PropertyConfigurator.configure(new FileInputStream(logFile));
            } catch (Exception e) {
                FRLogger.getLogger().info("Can't get the log file in lib folder and use default log.The error info:\n" + BIPrintUtils.outputException(e));
                useDefaultLog();
            }
        } else {
            useOriginalLog();
        }
//        }
    }

    private static void useOriginalLog() {
        URL original = Loader.getResource(LOAD_RESOURCE_NAME);
        if (original != null) {
            PropertyConfigurator.configure(original);
            FRContext.getLogger().info("The log properties url:" + original.toString());
        } else {
            useDefaultLog();
        }
    }

    private static void useDefaultLog() {
        FRContext.getLogger().info("The default log properties");
        PropertyConfigurator.configure(defaultProperties());
    }

    private static File getLibLogFile() {
        String webInfoPath = FRContext.getCurrentEnv().getPath();
        File webInfFile = new File(webInfoPath);
        if (webInfFile.exists()) {
            File[] childFiles = webInfFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return ComparatorUtils.equals(LIB_FOLDER, name);
                }
            });
            if (childFiles.length == 1) {
                File[] configs = childFiles[0].listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return ComparatorUtils.equals(LOG_PROPERTIES_URL, name);
                    }
                });
                if (configs.length == 1) {
                    return configs[0];
                }else {
                    FRContext.getLogger().info("The property file not found");
                    //System.out.println("The property file not found");
                }
            }else {
                FRContext.getLogger().info("The lib folder not found");
//                System.out.println("The lib folder not found");
            }

        }else {
            FRContext.getLogger().info("The basic WEB-INF not found");
//            System.out.println("The basic WEB-INF not found");
        }
        return null;
    }

    public static Map<Class, BILogger> loggerMap = new HashMap<Class, BILogger>();
    public static Map<String, Map<String, Object>> loggerCacheMap = new ConcurrentHashMap<String, Map<String, Object>>();

    public static BILogger getLogger(Class clazz) {
        if (loggerMap.containsKey(clazz)) {
            return loggerMap.get(clazz);
        } else {
            synchronized (BILoggerFactory.class) {
                if (!loggerMap.containsKey(clazz)) {
                    BILogger logger = new BILogger(LoggerFactory.getLogger(clazz));
                    loggerMap.put(clazz, logger);
                }
                return loggerMap.get(clazz);
            }
        }
    }

    public static BILogger getLogger() {
        return getLogger(BILogger.class);
    }

    public static InputStream defaultProperties() {
        String str = "log4j.rootLogger=INFO, ServerDailyRollingFile, stdout\n" +
                "\n" +
                "log4j.appender.ServerDailyRollingFile=org.apache.log4j.DailyRollingFileAppender\n" +
                "\n" +
                "log4j.appender.ServerDailyRollingFile.DatePattern='.'yyyy-MM-dd\n" +
                "\n" +
                "log4j.appender.ServerDailyRollingFile.File=logs/notify-subscription.log\n" +
                "\n" +
                "log4j.appender.ServerDailyRollingFile.layout=org.apache.log4j.PatternLayout\n" +
                "\n" +
                "log4j.appender.ServerDailyRollingFile.layout.ConversionPattern=%d - %m%n\n" +
                "\n" +
                "log4j.appender.ServerDailyRollingFile.Append=true\n" +
                "\n" +
                "log4j.appender.stdout=org.apache.log4j.ConsoleAppender\n" +
                "\n" +
                "log4j.appender.stdout.layout=org.apache.log4j.PatternLayout\n" +
                "\n" +
                "log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH\\:mm\\:ss} %p [%c] %m%n";

        return new ByteArrayInputStream(str.getBytes());
    }


    public static void cacheLoggerInfo(String cacheTag, String cacheSubTag, Object cacheContent) {
        Map<String, Object> cacheMap = BILoggerFactory.getSpecificCacheMap(cacheTag);
        cacheMap.put(cacheSubTag, cacheContent);
    }

    public static Map<String, Object> getSpecificCacheMap(String cacheTag) {
        if (loggerCacheMap.containsKey(cacheTag)) {
            return loggerCacheMap.get(cacheTag);
        } else {
            synchronized (BILoggerFactory.class) {
                if (!loggerCacheMap.containsKey(cacheTag)) {
                    loggerCacheMap.put(cacheTag, new HashMap<String, Object>());
                }
                return loggerCacheMap.get(cacheTag);
            }
        }
    }

    public static Object getLoggerCacheValue(String cacheTag, String cacheSubTag) {
        if (!loggerCacheMap.containsKey(cacheTag)) {
//            getLogger(BILoggerFactory.class).warn("\n" + "The LoggerInfoCache does not contains the cacheTag: " + cacheTag);
            return null;
        }

        Map specificCacheMap = getSpecificCacheMap(cacheTag);
        if (!specificCacheMap.containsKey(cacheSubTag)) {
//            getLogger(BILoggerFactory.class).warn("\n" + "The LoggerInfoCache contains the cacheTag: " + cacheTag + " but does not contains the subTag: " + cacheSubTag);
            return null;
        }
        return specificCacheMap.get(cacheSubTag);
    }

    public static void clearLoggerCacheValue(String cacheTag) {
        if (loggerCacheMap.containsKey(cacheTag)) {
            loggerCacheMap.get(cacheTag).clear();
            loggerCacheMap.remove(cacheTag);
        }
    }


}

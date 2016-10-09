package com.finebi.cube.common.log;

import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * BI日志输出
 */
public class BILoggerFactory {


    public static Map<Class, BILogger> loggerMap = new HashMap<Class, BILogger>();

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

}

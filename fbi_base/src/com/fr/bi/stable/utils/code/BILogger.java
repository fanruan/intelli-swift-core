package com.fr.bi.stable.utils.code;

/**
 * BI日志输出
 */
public class BILogger {

    public static BILogger logger = null;

    public static BILogger getLogger() {
        if (logger != null) {
            return logger;
        }
        synchronized (BILogger.class) {
            if (logger == null) {
                logger = new BILogger();
            }
        }
        return logger;
    }

    public void error(String message) {
        System.err.println(message);
    }

    public void error(String message, Throwable e) {
        System.err.println(message);
        e.printStackTrace();
    }

    public void info(String message) {
        System.out.println(message);
    }

    public void debug(String message) {
        System.out.println(message);
    }
}
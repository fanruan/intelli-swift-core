package com.fr.bi.stable.utils.code;

import com.fr.bi.stable.utils.time.BIDateUtils;

/**
 * BI日志输出
 */
public class BILogger {
    boolean verbose = true;
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
        System.err.println(BIDateUtils.getCurrentDateTime()+": "+message);
    }

    public void error(String message, Throwable e) {
        System.err.println(BIDateUtils.getCurrentDateTime()+": "+message);
        e.printStackTrace();
    }

    public void info(String message) {
        System.out.println(BIDateUtils.getCurrentDateTime()+": "+message);
    }

    public void debug(String message) {
        if (verbose) {
            System.out.println(BIDateUtils.getCurrentDateTime()+": "+message);
        }
    }
}

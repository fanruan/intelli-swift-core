package com.fr.bi.stable.utils.code;

import com.fr.bi.manager.PerformancePlugManager;
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
        errorOut(message);
    }

    public void error(String message, Throwable e) {
        errorOut(message);
        String out = BIPrintUtils.outputException(e);
        errorOut(out);
    }

    private void errorOut(String message) {
        if (PerformancePlugManager.getInstance().useStandardOutError()) {
            standardOut(message);
        } else {
            System.err.println(BIDateUtils.getCurrentDateTime() + ": " + message);
        }
    }

    public void info(String message) {
        standardOut(message);
    }

    public void standardOut(String message) {
        System.out.println(BIDateUtils.getCurrentDateTime() + ": " + message);
    }


    public void debug(String message) {
        if (verbose) {
            System.out.println(BIDateUtils.getCurrentDateTime() + ": " + message);
        }
    }
}

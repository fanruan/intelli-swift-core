package com.fr.swift.log;


import com.fr.log.FineLoggerFactory;
import com.fr.stable.StringUtils;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author anchore
 */
public class SwiftLogger {
    /**
     * date thread [class.method] message
     */
    private static final MessageFormat MSG_FORMAT = new MessageFormat("{0} {1} [{2}.{3}] {4}");

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yy-M-d H:m:s.S");

    public void debug(String msg) {
        FineLoggerFactory.getLogger().debug(getLog(msg));
    }

    public void info(String msg) {
        FineLoggerFactory.getLogger().info(getLog(msg));
    }

    public void warn(String msg) {
        FineLoggerFactory.getLogger().warn(getLog(msg));
    }

    public void warn(Throwable throwable) {
        FineLoggerFactory.getLogger().warn(getLog(StringUtils.EMPTY), throwable);
    }

    public void warn(String msg, Throwable throwable) {
        FineLoggerFactory.getLogger().warn(getLog(msg), throwable);
    }

    public void error(String msg) {
        FineLoggerFactory.getLogger().error(getLog(msg));
    }

    public void error(Throwable throwable) {
        FineLoggerFactory.getLogger().error(getLog(StringUtils.EMPTY), throwable);
    }

    public void error(String msg, Throwable throwable) {
        FineLoggerFactory.getLogger().error(getLog(msg), throwable);
    }

    private static String getLog(String msg) {
        Thread thread = Thread.currentThread();
        StackTraceElement stackTrace = thread.getStackTrace()[3];
        return MSG_FORMAT.format(new Object[]{
                DATE_FORMAT.format(new Date()),
                thread.getName(),
                stackTrace.getClassName(),
                stackTrace.getMethodName(),
                msg
        });
    }
}
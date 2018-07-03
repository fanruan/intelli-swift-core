package com.fr.swift.log;


import com.fr.log.FineLoggerFactory;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author anchore
 */
public class SwiftLogger {
    /**
     * date thread level [class.method] message\n
     */
    private static final MessageFormat MSG_FORMAT = new MessageFormat("{0} {1} {2} [{3}.{4}] {5}");

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yy-M-d H:m:s.S");

    public void debug(String msg) {
        Thread thread = Thread.currentThread();
        StackTraceElement stackTrace = thread.getStackTrace()[2];
        FineLoggerFactory.getLogger().debug(MSG_FORMAT.format(new Object[]{
                DATE_FORMAT.format(new Date()),
                thread.getName(),
                Level.DEBUG,
                stackTrace.getClassName(),
                stackTrace.getMethodName(),
                msg
        }));
    }

    public void info(String msg) {
        Thread thread = Thread.currentThread();
        StackTraceElement stackTrace = thread.getStackTrace()[2];
        FineLoggerFactory.getLogger().info(MSG_FORMAT.format(new Object[]{
                DATE_FORMAT.format(new Date()),
                thread.getName(),
                Level.INFO,
                stackTrace.getClassName(),
                stackTrace.getMethodName(),
                msg
        }));
    }

    public void warn(String msg) {
        warn(msg, null);
    }

    public void warn(Throwable throwable) {
        warn(null, throwable);
    }

    public void warn(String msg, Throwable throwable) {
        Thread thread = Thread.currentThread();
        StackTraceElement stackTrace = thread.getStackTrace()[2];
        FineLoggerFactory.getLogger().warn(MSG_FORMAT.format(new Object[]{
                DATE_FORMAT.format(new Date()),
                thread.getName(),
                Level.WARN,
                stackTrace.getClassName(),
                stackTrace.getMethodName(),
                msg
        }), throwable);
    }

    public void error(String msg) {
        error(msg, null);
    }

    public void error(Throwable throwable) {
        error(null, throwable);
    }

    public void error(String msg, Throwable throwable) {
        Thread thread = Thread.currentThread();
        StackTraceElement stackTrace = thread.getStackTrace()[2];
        FineLoggerFactory.getLogger().error(MSG_FORMAT.format(new Object[]{
                DATE_FORMAT.format(new Date()),
                thread.getName(),
                Level.ERROR,
                stackTrace.getClassName(),
                stackTrace.getMethodName(),
                msg
        }), throwable);
    }
}
package com.fr.swift.log;


import com.fr.third.apache.log4j.Level;
import com.fr.third.apache.log4j.Logger;

/**
 * @author anchore
 */
public class SwiftLogger {
    private static final String FQCN = SwiftLogger.class.getName();

    private final Logger logger;

    SwiftLogger(Logger logger) {
        this.logger = logger;
    }

    public String getName() {
        return logger.getName();
    }

    public void trace(String msg) {
        trace(msg, null);
    }

    public void trace(Throwable throwable) {
        trace(null, throwable);
    }

    public void trace(String msg, Throwable throwable) {
        logger.log(FQCN, Level.TRACE, msg, throwable);
    }

    public void debug(String msg) {
        debug(msg, null);
    }

    public void debug(Throwable throwable) {
        debug(null, throwable);
    }

    public void debug(String msg, Throwable throwable) {
        logger.log(FQCN, Level.DEBUG, msg, throwable);
    }

    public void info(String msg) {
        info(msg, null);
    }

    public void info(Throwable throwable) {
        info(null, throwable);
    }

    public void info(String msg, Throwable throwable) {
        logger.log(FQCN, Level.INFO, msg, throwable);
    }

    public void warn(String msg) {
        warn(msg, null);
    }

    public void warn(Throwable throwable) {
        warn(null, throwable);
    }

    public void warn(String msg, Throwable throwable) {
        logger.log(FQCN, Level.WARN, msg, throwable);
    }

    public void error(String msg) {
        error(msg, null);
    }

    public void error(Throwable throwable) {
        error(null, throwable);
    }

    public void error(String msg, Throwable throwable) {
        logger.log(FQCN, Level.ERROR, msg, throwable);
    }

}

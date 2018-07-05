package com.fr.swift.log;

import com.fr.third.apache.log4j.Logger;
import com.fr.third.apache.log4j.Priority;

/**
 * @author anchore
 * @date 2018/7/4
 */
class SwiftLog4jLogger extends BaseSwiftLogger implements SwiftLogger {
    private Logger logger;
    String FQCN = "";
    private boolean traceCapable;


    SwiftLog4jLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String s) {
        if (isTraceEnabled()) {
            logger.trace(s);
        }
    }

    @Override
    public void trace(String s, Object o) {
        if (isTraceEnabled()) {
            logger.trace(format(s, o));
        }
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        if (isTraceEnabled()) {
            logger.trace(format(s, o, o1));
        }
    }

    @Override
    public void trace(String s, Object... objects) {
        if (isTraceEnabled()) {
            logger.trace(format(s, objects));
        }
    }

    @Override
    public void trace(String s, Throwable throwable) {
        if (isTraceEnabled()) {
            logger.trace(s, throwable);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        if (isDebugEnabled()) {
            logger.debug(s);
        }
    }

    @Override
    public void debug(String s, Object o) {
        if (isDebugEnabled()) {
            logger.debug(format(s, o));
        }
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        if (isDebugEnabled()) {
            logger.debug(format(s, o, o1));
        }
    }

    @Override
    public void debug(String s, Object... objects) {
        if (isDebugEnabled()) {
            logger.debug(format(s, objects));
        }
    }

    @Override
    public void debug(String s, Throwable throwable) {
        if (isDebugEnabled()) {
            logger.debug(s, throwable);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String s) {
        if (isInfoEnabled()) {
            logger.info(s);
        }
    }

    @Override
    public void info(String s, Object o) {
        if (isInfoEnabled()) {
            logger.info(format(s, o));
        }
    }

    @Override
    public void info(String s, Object o, Object o1) {
        if (isInfoEnabled()) {
            logger.info(format(s, o, o1));
        }
    }

    @Override
    public void info(String s, Object... objects) {
        if (isInfoEnabled()) {
            logger.info(format(s, objects));
        }
    }

    @Override
    public void info(String s, Throwable throwable) {
        if (isInfoEnabled()) {
            logger.info(s, throwable);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isEnabledFor(Priority.WARN);
    }

    @Override
    public void warn(String s) {
        if (isWarnEnabled()) {
            logger.warn(s);
        }
    }

    @Override
    public void warn(String s, Object o) {
        if (isWarnEnabled()) {
            logger.warn(format(s, o));
        }
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        if (isWarnEnabled()) {
            logger.warn(format(s, o, o1));
        }
    }

    @Override
    public void warn(String s, Object... objects) {
        if (isWarnEnabled()) {
            logger.warn(format(s, objects));
        }
    }

    @Override
    public void warn(String s, Throwable throwable) {
        if (isWarnEnabled()) {
            logger.warn(s, throwable);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isEnabledFor(Priority.ERROR);
    }

    @Override
    public void error(String s) {
        if (isErrorEnabled()) {
            logger.error(s);
        }
    }

    @Override
    public void error(String s, Object o) {
        if (isErrorEnabled()) {
            logger.error(format(s, o));
        }
    }

    @Override
    public void error(String s, Object o, Object o1) {
        if (isErrorEnabled()) {
            logger.error(format(s, o, o1));
        }
    }

    @Override
    public void error(String s, Object... objects) {
        if (isErrorEnabled()) {
            logger.error(format(s, objects));
        }
    }

    @Override
    public void error(String s, Throwable throwable) {
        if (isErrorEnabled()) {
            logger.error(s, throwable);
        }
    }

    @Override
    public Type getType() {
        return Type.LOG4J;
    }
}
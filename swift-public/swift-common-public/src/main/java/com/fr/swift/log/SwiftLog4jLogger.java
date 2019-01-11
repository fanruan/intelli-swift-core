package com.fr.swift.log;

import com.fr.swift.log.impl.BaseSwiftLogger;
import com.fr.swift.util.Strings;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.helpers.FormattingTuple;

/**
 * @author anchore
 * @date 2018/7/4
 */
class SwiftLog4jLogger extends BaseSwiftLogger implements SwiftLogger {

    private static final String FQCN = SwiftLog4jLogger.class.getName();

    private Logger logger;

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
            logger.log(FQCN, Level.TRACE, s, null);
        }
    }

    @Override
    public void trace(String s, Object o) {
        if (isTraceEnabled()) {
            FormattingTuple tuple = format(s, o);
            logger.log(FQCN, Level.TRACE, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        if (isTraceEnabled()) {
            FormattingTuple tuple = format(s, o, o1);
            logger.log(FQCN, Level.TRACE, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void trace(String s, Object... objects) {
        if (isTraceEnabled()) {
            FormattingTuple tuple = format(s, objects);
            logger.log(FQCN, Level.TRACE, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void trace(String s, Throwable throwable) {
        if (isTraceEnabled()) {
            logger.log(FQCN, Level.TRACE, s, throwable);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        if (isDebugEnabled()) {
            logger.log(FQCN, Level.DEBUG, s, null);
        }
    }

    @Override
    public void debug(String s, Object o) {
        if (isDebugEnabled()) {
            FormattingTuple tuple = format(s, o);
            logger.log(FQCN, Level.DEBUG, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        if (isDebugEnabled()) {
            FormattingTuple tuple = format(s, o, o1);
            logger.log(FQCN, Level.DEBUG, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void debug(String s, Object... objects) {
        if (isDebugEnabled()) {
            FormattingTuple tuple = format(s, objects);
            logger.log(FQCN, Level.DEBUG, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void debug(String s, Throwable throwable) {
        if (isDebugEnabled()) {
            logger.log(FQCN, Level.DEBUG, s, throwable);
        }
    }

    @Override
    public void debug(Throwable t) {
        debug(Strings.EMPTY, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String s) {
        if (isInfoEnabled()) {
            logger.log(FQCN, Level.INFO, s, null);
        }
    }

    @Override
    public void info(String s, Object o) {
        if (isInfoEnabled()) {
            FormattingTuple tuple = format(s, o);
            logger.log(FQCN, Level.INFO, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void info(String s, Object o, Object o1) {
        if (isInfoEnabled()) {
            FormattingTuple tuple = format(s, o, o1);
            logger.log(FQCN, Level.INFO, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void info(String s, Object... objects) {
        if (isInfoEnabled()) {
            FormattingTuple tuple = format(s, objects);
            logger.log(FQCN, Level.INFO, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void info(String s, Throwable throwable) {
        if (isInfoEnabled()) {
            logger.log(FQCN, Level.INFO, s, throwable);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isEnabledFor(Level.WARN);
    }

    @Override
    public void warn(String s) {
        if (isWarnEnabled()) {
            logger.log(FQCN, Level.WARN, s, null);
        }
    }

    @Override
    public void warn(String s, Object o) {
        if (isWarnEnabled()) {
            FormattingTuple tuple = format(s, o);
            logger.log(FQCN, Level.WARN, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        if (isWarnEnabled()) {
            FormattingTuple tuple = format(s, o, o1);
            logger.log(FQCN, Level.WARN, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void warn(String s, Object... objects) {
        if (isWarnEnabled()) {
            FormattingTuple tuple = format(s, objects);
            logger.log(FQCN, Level.WARN, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void warn(String s, Throwable throwable) {
        if (isWarnEnabled()) {
            logger.log(FQCN, Level.WARN, s, throwable);
        }
    }

    @Override
    public void warn(Throwable t) {
        warn(Strings.EMPTY, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isEnabledFor(Level.ERROR);
    }

    @Override
    public void error(String s) {
        if (isErrorEnabled()) {
            logger.log(FQCN, Level.ERROR, s, null);
        }
    }

    @Override
    public void error(String s, Object o) {
        if (isErrorEnabled()) {
            FormattingTuple tuple = format(s, o);
            logger.log(FQCN, Level.ERROR, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void error(String s, Object o, Object o1) {
        if (isErrorEnabled()) {
            FormattingTuple tuple = format(s, o, o1);
            logger.log(FQCN, Level.WARN, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void error(String s, Object... objects) {
        if (isErrorEnabled()) {
            FormattingTuple tuple = format(s, objects);
            logger.log(FQCN, Level.WARN, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void error(String s, Throwable throwable) {
        if (isErrorEnabled()) {
            logger.log(FQCN, Level.ERROR, s, throwable);
        }
    }

    @Override
    public void error(Throwable t) {
        error(Strings.EMPTY, t);
    }
}
package com.fr.swift.log;

import com.fr.log.FineLoggerProvider;

/**
 * @author anchore
 * @date 2018/7/4
 */
class SwiftFrLogger extends BaseSwiftLogger implements SwiftLogger {
    private FineLoggerProvider logger;

    SwiftFrLogger(FineLoggerProvider logger) {
        this.logger = logger;
    }

    @Override
    public String getName() {
        return logger.toString();
    }

    @Override
    public void trace(String s) {
    }

    @Override
    public void trace(String s, Object o) {
    }

    @Override
    public void trace(String s, Object o, Object o1) {
    }

    @Override
    public void trace(String s, Object... objects) {
    }

    @Override
    public void trace(String s, Throwable throwable) {
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
            logger.debug(s, o);
        }
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        if (isDebugEnabled()) {
            logger.debug(s, o, o1);
        }
    }

    @Override
    public void debug(String s, Object... objects) {
        if (isDebugEnabled()) {
            logger.debug(s, objects);
        }
    }

    @Override
    public void debug(String s, Throwable throwable) {
        if (isDebugEnabled()) {
            logger.debug(s, throwable);
        }
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
            logger.info(s, o);
        }
    }

    @Override
    public void info(String s, Object o, Object o1) {
        if (isInfoEnabled()) {
            logger.info(s, o, o1);
        }
    }

    @Override
    public void info(String s, Object... objects) {
        if (isInfoEnabled()) {
            logger.info(s, objects);
        }
    }

    @Override
    public void info(String s, Throwable throwable) {
        if (isInfoEnabled()) {
            logger.info(s, throwable);
        }
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
        return Type.FR;
    }
}
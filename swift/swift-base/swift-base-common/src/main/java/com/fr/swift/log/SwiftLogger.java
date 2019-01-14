package com.fr.swift.log;

import com.fineio.logger.FineIOLogger;

/**
 * @author anchore
 * @date 2018/7/4
 */
public interface SwiftLogger extends FineIOLogger {

    boolean isDebugEnabled();

    void debug(String msg, Object... args);

    @Override
    void debug(String msg);

    void debug(Throwable t);

    boolean isInfoEnabled();

    void info(String msg, Object... args);

    @Override
    void info(String msg);

    boolean isWarnEnabled();

    void warn(String msg, Object... args);

    void warn(String msg);

    void warn(Throwable t);

    boolean isErrorEnabled();

    void error(String msg, Object... args);

    @Override
    void error(String msg);

    @Override
    void error(Throwable t);
}
package com.fr.swift.log;

/**
 * @author anchore
 * @date 2018/7/4
 */
public interface SwiftLogger {

    boolean isDebugEnabled();

    void debug(String msg, Object... args);

    void debug(String msg);

    void debug(Throwable t);

    boolean isInfoEnabled();

    void info(String msg, Object... args);

    void info(String msg);

    boolean isWarnEnabled();

    void warn(String msg, Object... args);

    void warn(String msg);

    void warn(Throwable t);

    boolean isErrorEnabled();

    void error(String msg, Object... args);

    void error(String msg);

    void error(Throwable t);
}
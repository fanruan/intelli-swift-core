package com.fr.swift.jdbc.exception;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/11/16
 */
public final class Exceptions {
    public static SQLException urlEmpty() {
        return new URLEmptyException();
    }

    public static SQLException sqlIncorrect(String sql) {
        return new SqlIncorrectException(sql);
    }

    public static SQLException urlFormat(String url) {
        return new URLFormatException(url);
    }

    public static SQLException sql(String msg) {
        return new SQLException(msg);
    }

    public static RuntimeException runtime(String msg) {
        return new RuntimeException(msg);
    }

    public static RuntimeException runtime(String msg, Throwable t) {
        return new RuntimeException(msg, t);
    }

    public static SQLException notSupported(Throwable t) {
        return new JdbcNotSupportedException(t);
    }

    public static SQLException notSupported(String msg) {
        return new JdbcNotSupportedException(msg);
    }

    public static SQLException notSupported(String msg, Throwable t) {
        return new JdbcNotSupportedException(msg, t);
    }

    public static RuntimeException noCodecResponse() {
        return new NoCodecResponseException();
    }

    public static ConnectionTimeoutException timeout() {
        return new ConnectionTimeoutException("Connection timeout.");
    }

    public static ConnectionTimeoutException timeout(String msg, Throwable t) {
        return new ConnectionTimeoutException(msg, t);
    }

    public static ConnectionTimeoutException timeout(String msg) {
        return new ConnectionTimeoutException(msg);
    }

    public static SQLException environment(Throwable t) {
        return new EnvironmentException(t);
    }

    public static SQLException sql(Exception e) {
        return new SQLException(e);
    }
}

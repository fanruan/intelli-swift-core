package com.swift.jdbc.exception;

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
}

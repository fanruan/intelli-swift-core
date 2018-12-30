package com.fr.swift.jdbc.exception;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/11/16
 */
class SqlIncorrectException extends SQLException {
    private static final long serialVersionUID = -750482748195544463L;

    SqlIncorrectException(String sql, Throwable throwable) {
        super(String.format("Sql %s is incorrect", sql), throwable);
    }
}

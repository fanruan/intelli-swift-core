package com.fr.swift.jdbc.exception;

import java.sql.SQLException;

/**
 * Created by pony on 2018/8/17.
 */
public class SwiftJDBCNotSupportedException extends SQLException {

    public SwiftJDBCNotSupportedException() {
    }

    public SwiftJDBCNotSupportedException(String message) {
        super(message);
    }

    public SwiftJDBCNotSupportedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

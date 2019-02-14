package com.fr.swift.jdbc.exception;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/11/19
 */
class JdbcNotSupportedException extends SQLException {
    private static final long serialVersionUID = 4597053950384420778L;

    JdbcNotSupportedException(Throwable cause) {
        super(cause);
    }

    JdbcNotSupportedException(String reason) {
        super(reason);
    }

    public JdbcNotSupportedException(String reason, Throwable cause) {
        super(reason, cause);
    }
}

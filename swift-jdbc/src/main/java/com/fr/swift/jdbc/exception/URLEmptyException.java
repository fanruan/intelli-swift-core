package com.fr.swift.jdbc.exception;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/26
 */
class URLEmptyException extends SQLException {
    private static final long serialVersionUID = 1635067327918992876L;

    URLEmptyException() {
        super("URL can not be null!");
    }
}

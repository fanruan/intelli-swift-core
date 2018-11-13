package com.fr.swift.jdbc.exception;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/26
 */
public class URLEmptyException extends SQLException {
    public URLEmptyException() {
        super("URL can not be null!");
    }
}

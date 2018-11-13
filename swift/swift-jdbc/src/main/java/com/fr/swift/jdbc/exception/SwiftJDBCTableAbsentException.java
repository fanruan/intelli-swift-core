package com.fr.swift.jdbc.exception;

import java.sql.SQLException;

/**
 * Created by pony on 2018/8/20.
 */
public class SwiftJDBCTableAbsentException extends SQLException {
    public SwiftJDBCTableAbsentException( String tableName) {
        super(String.format("no such table %s !",  tableName));
    }
}

package com.fr.swift.jdbc.exception;

import java.sql.SQLException;

/**
 * Created by pony on 2018/8/20.
 */
public class SwiftJDBCColumnAbsentException extends SQLException {
    public SwiftJDBCColumnAbsentException( String columnName) {
        super(String.format("no such column %s !",  columnName));
    }

}

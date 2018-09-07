package com.fr.swift.db;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/8/22
 */
public class NoSuchTableException extends SQLException {
    public NoSuchTableException(String reason) {
        super(reason);
    }
}
package com.fr.swift.db;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/8/23
 */
public class AlreadyExistsException extends SQLException {
    public AlreadyExistsException(String reason) {
        super(reason);
    }
}
package com.fr.swift.exception.meta;

import java.sql.SQLException;

/**
 * Created by pony on 2017/12/6.
 */
public class SwiftDataModelException extends SQLException {
    public SwiftDataModelException(Exception e) {
        super(e.getMessage());
    }
}

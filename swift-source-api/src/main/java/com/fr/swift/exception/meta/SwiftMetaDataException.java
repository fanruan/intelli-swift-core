package com.fr.swift.exception.meta;

import java.sql.SQLException;

/**
 * Created by pony on 2017/11/20.
 */
public class SwiftMetaDataException extends SQLException {
    public SwiftMetaDataException() {
    }

    public SwiftMetaDataException(String reason) {
        super(reason);
    }
}

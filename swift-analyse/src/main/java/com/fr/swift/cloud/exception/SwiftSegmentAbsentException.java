package com.fr.swift.cloud.exception;

import java.sql.SQLException;

/**
 * Created by pony on 2017/12/14.
 */
public class SwiftSegmentAbsentException extends SQLException {
    public SwiftSegmentAbsentException(String reason) {
        super(reason);
    }
}

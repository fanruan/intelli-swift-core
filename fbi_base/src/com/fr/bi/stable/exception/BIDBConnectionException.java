package com.fr.bi.stable.exception;

/**
 * Created by Connery on 2016/1/23.
 */
public class BIDBConnectionException extends Exception {
    private static final long serialVersionUID = -5030082314447324100L;

    public BIDBConnectionException() {
    }

    public BIDBConnectionException(String message) {
        super(message);
    }
}
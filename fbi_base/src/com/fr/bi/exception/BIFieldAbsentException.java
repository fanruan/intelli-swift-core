package com.fr.bi.exception;

/**
 * Created by Connery on 2015/12/28.
 */
public class BIFieldAbsentException extends Exception {
    public BIFieldAbsentException(String message) {
        super(message);
    }

    public BIFieldAbsentException() {
    }
}
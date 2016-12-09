package com.fr.bi.exception;

/**
 * Created by Connery on 2015/12/28.
 */
public class BIFieldAbsentException extends Exception {
    private static final long serialVersionUID = -2938083590827102489L;

    public BIFieldAbsentException(String message) {
        super(message);
    }

    public BIFieldAbsentException() {
    }
}
package com.fr.bi.exception;

/**
 * Created by Connery on 2015/12/28.
 */
public class BIKeyAbsentException extends Exception {
    public BIKeyAbsentException(String message) {
        super(message);
    }

    public BIKeyAbsentException() {
    }
}
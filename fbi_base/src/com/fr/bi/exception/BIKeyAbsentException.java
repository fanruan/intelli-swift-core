package com.fr.bi.exception;

/**
 * Created by Connery on 2015/12/28.
 */
public class BIKeyAbsentException extends Exception {
    private static final long serialVersionUID = -7389209618459687686L;

    public BIKeyAbsentException(String message) {
        super(message);
    }

    public BIKeyAbsentException() {
    }
}
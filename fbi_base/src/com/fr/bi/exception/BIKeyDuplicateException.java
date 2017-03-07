package com.fr.bi.exception;

/**
 * Created by Connery on 2015/12/28.
 */
public class BIKeyDuplicateException extends Exception {
    private static final long serialVersionUID = -4105292475217696974L;

    public BIKeyDuplicateException(String message) {
        super(message);
    }

    public BIKeyDuplicateException() {
    }
}
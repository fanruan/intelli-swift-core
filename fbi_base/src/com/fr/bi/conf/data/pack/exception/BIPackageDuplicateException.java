package com.fr.bi.conf.data.pack.exception;

/**
 * Created by Connery on 2016/1/7.
 * 业务包重复异常
 */
public class BIPackageDuplicateException extends Exception {

    private static final long serialVersionUID = 1344825589320990371L;

    public BIPackageDuplicateException(String message) {

        super(message);
    }

    public BIPackageDuplicateException() {

    }
}
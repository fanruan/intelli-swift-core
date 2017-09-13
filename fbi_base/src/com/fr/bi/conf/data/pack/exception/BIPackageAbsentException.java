package com.fr.bi.conf.data.pack.exception;

/**
 * Created by Connery on 2016/1/7.
 * 业务包不存在异常
 */
public class BIPackageAbsentException extends Exception {

    private static final long serialVersionUID = 6432915344150465956L;

    public BIPackageAbsentException(String message) {

        super(message);
    }

    public BIPackageAbsentException() {

    }
}
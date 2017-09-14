package com.fr.bi.conf.data.pack.exception;

/**
 * Created by Connery on 2016/1/7.
 * 分组不存在异常
 */
public class BIGroupAbsentException extends Exception {

    private static final long serialVersionUID = -4897975690182800697L;

    public BIGroupAbsentException(String message) {

        super(message);
    }

    public BIGroupAbsentException() {

    }
}
package com.fr.bi.conf.data.pack.exception;

/**
 * Created by Connery on 2016/1/7.
 * 分组重复异常
 */
public class BIGroupDuplicateException extends Exception {

    private static final long serialVersionUID = 2244727049394140356L;

    public BIGroupDuplicateException(String message) {

        super(message);
    }

    public BIGroupDuplicateException() {

    }
}
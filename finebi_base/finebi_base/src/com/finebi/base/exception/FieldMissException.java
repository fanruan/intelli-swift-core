package com.finebi.base.exception;

/**
 * Created by andrew_asa on 2017/9/29.
 * 没有该字段异常
 */
public class FieldMissException extends Exception {

    public FieldMissException(String msg) {

        super(msg);
    }

    public FieldMissException() {

        super();
    }
}

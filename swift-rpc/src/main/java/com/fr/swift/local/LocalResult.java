package com.fr.swift.local;

/**
 * This class created on 2018/9/5
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalResult {

    private Object result;
    private Throwable exception;

    public LocalResult() {
    }

    public LocalResult(Object result, Throwable exception) {
        this.result = result;
        this.exception = exception;
    }

    public Object get() {
        return this.result;
    }

    public Throwable getException() {
        return this.exception;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}

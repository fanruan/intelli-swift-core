package com.fr.swift.cloud.basic;

import java.io.Serializable;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftResponse implements Response, Serializable {

    private static final long serialVersionUID = -2376912555513832143L;
    private String requestId;
    private Throwable exception;
    private Object result;

    @Override
    public boolean hasException() {
        return exception != null;
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public boolean isError() {
        return exception != null;
    }

}

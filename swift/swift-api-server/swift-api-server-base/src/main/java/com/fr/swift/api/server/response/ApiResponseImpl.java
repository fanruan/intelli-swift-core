package com.fr.swift.api.server.response;

import com.fr.swift.api.server.response.error.ServerErrorCode;
import com.fr.swift.util.Strings;

import java.io.Serializable;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ApiResponseImpl implements ApiResponse {

    private static final long serialVersionUID = -8293646149558564100L;

    private Serializable result;

    private Throwable throwable;

    private int statusCode = ServerErrorCode.SERVER_OK;

    public ApiResponseImpl() {
    }

    public ApiResponseImpl(Serializable result) {
        this.result = result;
    }

    public ApiResponseImpl(Throwable throwable) {
        this.throwable = throwable;
        this.statusCode = ServerErrorCode.SERVER_UNKNOWN_ERROR;

    }

    @Override
    public int statusCode() {
        return statusCode;
    }

    @Override
    public String description() {
        return throwable != null ? throwable.getMessage() : Strings.EMPTY;
    }

    @Override
    public boolean isError() {
        return throwable != null;
    }

    @Override
    public Serializable result() {
        return result;
    }

    @Override
    public void setResult(Serializable result) {
        this.result = result;
    }

    @Override
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}

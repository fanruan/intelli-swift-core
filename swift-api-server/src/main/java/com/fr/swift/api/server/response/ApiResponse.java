package com.fr.swift.api.server.response;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018-12-07
 */
public interface ApiResponse extends Serializable {
    int UNKNOWN_ERROR = 500000;

    /**
     * 状态码
     *
     * @return
     */
    int statusCode();

    /**
     * 异常描述
     *
     * @return
     */
    String description();

    /**
     * 是否错误
     *
     * @return
     */
    boolean isError();

    /**
     * 返回的结果
     *
     * @return
     */
    Serializable result();

    void setResult(Serializable result);

    void setThrowable(Throwable throwable);

    void setStatusCode(int statusCode);
}

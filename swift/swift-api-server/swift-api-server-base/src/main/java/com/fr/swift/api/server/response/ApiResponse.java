package com.fr.swift.api.server.response;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018-12-07
 */
public interface ApiResponse extends Serializable {
    /**
     * 错误码
     *
     * @return
     */
    ErrorCode errorCode();

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

    /**
     * 这个可以enum实现
     */
    interface ErrorCode extends Serializable {
        /**
         * 错误码
         *
         * @return
         */
        String getCode();

        /**
         * 错误描述
         *
         * @return
         */
        String getDescription();
    }
}

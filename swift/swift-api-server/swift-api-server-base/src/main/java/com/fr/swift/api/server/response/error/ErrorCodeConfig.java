package com.fr.swift.api.server.response.error;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 */
public interface ErrorCodeConfig {

    /**
     * 服务器内部错误
     */
    int SERVER_INTERNAL_ERROR_CODE_BASE = 100000;

    /**
     * 参数类错误
     */
    int PARSE_ERROR_CODE_BASE = 200000;

    /**
     * SQL错误
     */
    int SQL_ERROR_CODE_BASE = 300000;

}
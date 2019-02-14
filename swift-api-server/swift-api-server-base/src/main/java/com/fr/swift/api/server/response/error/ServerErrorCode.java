package com.fr.swift.api.server.response.error;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ServerErrorCode {

    /**
     * 正常状态
     */
    int SERVER_OK = 0;

    /**
     * 服务器内部错误
     */
    int SERVER_UNKNOWN_ERROR = ErrorCodeConfig.SERVER_INTERNAL_ERROR_CODE_BASE + 1;

    /**
     * 服务器方法调用错误
     */
    int SERVER_INVOKE_ERROR = ErrorCodeConfig.SERVER_INTERNAL_ERROR_CODE_BASE + 2;
}

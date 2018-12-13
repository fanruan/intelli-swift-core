package com.fr.swift.api.server.response.error;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 */
public interface ParamErrorCode {
    /**
     * 参数resultType缺失错误
     */
    int RESULT_TYPE_ABSENT = ErrorCodeConfig.PARSE_ERROR_CODE_BASE + 1;

    /**
     * 参数json解析错误
     */
    int PARAMS_PARSER_ERROR = ErrorCodeConfig.PARSE_ERROR_CODE_BASE + 2;

    /**
     * 参数sql解析错误
     */
    int SQL_PARSE_ERROR = ErrorCodeConfig.SQL_ERROR_CODE_BASE + 1;
}

package com.fr.swift.api.server.exception;

import com.fr.swift.api.server.response.error.SqlErrorCode;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 */
public class ApiCrasher {

    public static <T> T crash(int errorCode) {
        throw new ApiRequestRuntimeException(errorCode);
    }

    public static <T> T crash(int errorCode, String msg) {
        throw new ApiRequestRuntimeException(errorCode, msg);
    }

    public static <T> T crash(int errorCode, Throwable t) {
        return crash(errorCode, t.getMessage(), t);
    }

    public static <T> T crash(int errorCode, String msg, Throwable t) {
        solveThrowable(errorCode, t);
        throw new ApiRequestRuntimeException(errorCode, msg, t);
    }

    private static void solveThrowable(int errorCode, Throwable t) {
        if (t instanceof InvocationTargetException) {
            Throwable target = ((InvocationTargetException) t).getTargetException();
            if (target instanceof SQLException) {
                throw new ApiRequestRuntimeException(SqlErrorCode.SQL_COMMON_ERROR, target);
            }
            // TODO: 2018/12/10  Exception判断
            throw new ApiRequestRuntimeException(errorCode, target);
        }
    }
}

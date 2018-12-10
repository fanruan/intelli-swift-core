package com.fr.swift.server.exception;

import com.fr.swift.api.server.exception.ApiCrasher;
import com.fr.swift.api.server.exception.ApiRequestRuntimeException;
import com.fr.swift.api.server.response.error.ServerErrorCode;
import com.fr.swift.api.server.response.error.SqlErrorCode;
import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 */
public class ApiCrasherTest extends TestCase {

    public void testErrorCode() {
        try {
            ApiCrasher.crash(ServerErrorCode.SERVER_UNKNOWN_ERROR);
        } catch (ApiRequestRuntimeException e) {
            assertEquals(e.getStatusCode(), ServerErrorCode.SERVER_UNKNOWN_ERROR);
            assertNull(e.getMessage());
        }
    }

    public void testErrorAndMsg() {
        try {
            ApiCrasher.crash(ServerErrorCode.SERVER_UNKNOWN_ERROR, "testErrorAndMsg");
        } catch (ApiRequestRuntimeException e) {
            assertEquals(e.getStatusCode(), ServerErrorCode.SERVER_UNKNOWN_ERROR);
            assertEquals(e.getMessage(), "testErrorAndMsg");
        }
    }

    public void testCommonException() {
        try {
            ApiCrasher.crash(ServerErrorCode.SERVER_UNKNOWN_ERROR, new Exception("testCommonException"));
        } catch (ApiRequestRuntimeException e) {
            assertEquals(e.getStatusCode(), ServerErrorCode.SERVER_UNKNOWN_ERROR);
            assertEquals(e.getMessage(), "testCommonException");
        }
    }

    public void testInvocationTargetException() {
        try {
            ApiCrasher.crash(ServerErrorCode.SERVER_UNKNOWN_ERROR, new InvocationTargetException(new Exception("testInvocationTargetException")));
        } catch (ApiRequestRuntimeException e) {
            assertEquals(e.getStatusCode(), ServerErrorCode.SERVER_UNKNOWN_ERROR);
            assertEquals(e.getMessage(), "testInvocationTargetException");
        }

        try {
            ApiCrasher.crash(ServerErrorCode.SERVER_UNKNOWN_ERROR, new InvocationTargetException(new SQLException("sqltest")));
        } catch (ApiRequestRuntimeException e) {
            assertEquals(e.getStatusCode(), SqlErrorCode.SQL_COMMON_ERROR);
            assertEquals(e.getMessage(), "sqltest");
        }
    }
}


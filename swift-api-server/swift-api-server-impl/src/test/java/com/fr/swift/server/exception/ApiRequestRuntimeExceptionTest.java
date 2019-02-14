package com.fr.swift.server.exception;

import com.fr.swift.api.server.exception.ApiRequestRuntimeException;
import com.fr.swift.api.server.response.error.ServerErrorCode;
import junit.framework.TestCase;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 */
public class ApiRequestRuntimeExceptionTest extends TestCase {
    public void testException() {
        try {
            throw new ApiRequestRuntimeException(ServerErrorCode.SERVER_UNKNOWN_ERROR);
        } catch (ApiRequestRuntimeException e) {
            assertEquals(e.getStatusCode(), ServerErrorCode.SERVER_UNKNOWN_ERROR);
            assertNull(e.getMessage());
        }
    }

    public void testInnerException() {
        try {
            throw new ApiRequestRuntimeException(ServerErrorCode.SERVER_UNKNOWN_ERROR, new Exception("test").getMessage());
        } catch (ApiRequestRuntimeException e) {
            assertEquals(e.getStatusCode(), ServerErrorCode.SERVER_UNKNOWN_ERROR);
            assertEquals(e.getMessage(),"test");
        }
    }
}

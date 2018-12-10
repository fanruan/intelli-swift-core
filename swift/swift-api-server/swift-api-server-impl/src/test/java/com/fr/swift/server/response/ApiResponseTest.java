package com.fr.swift.server.response;

import com.fr.swift.api.server.response.ApiResponse;
import com.fr.swift.api.server.response.ApiResponseImpl;
import com.fr.swift.api.server.response.error.ServerErrorCode;
import junit.framework.TestCase;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ApiResponseTest extends TestCase {

    public void testResult() {
        ApiResponse apiResponse = new ApiResponseImpl("12345");
        assertEquals(apiResponse.result(), "12345");
        assertFalse(apiResponse.isError());
        assertEquals(apiResponse.statusCode(), ServerErrorCode.SERVER_OK);
        assertEquals(apiResponse.description(), "");

        apiResponse.setThrowable(new Exception("testException"));
        apiResponse.setStatusCode(ServerErrorCode.SERVER_UNKNOWN_ERROR);
        assertEquals(apiResponse.statusCode(), ServerErrorCode.SERVER_UNKNOWN_ERROR);
        assertEquals(apiResponse.description(), "testException");
        assertTrue(apiResponse.isError());
    }

    public void testException() {
        ApiResponse apiResponse = new ApiResponseImpl(new Exception("testException"));
        assertNull(apiResponse.result());
        assertTrue(apiResponse.isError());
        assertEquals(apiResponse.statusCode(), ServerErrorCode.SERVER_UNKNOWN_ERROR);
        assertEquals(apiResponse.description(), "testException");

        apiResponse.setResult("12345");
        apiResponse.setStatusCode(ServerErrorCode.SERVER_OK);
        assertEquals(apiResponse.result(), "12345");
        assertEquals(apiResponse.statusCode(), ServerErrorCode.SERVER_OK);
    }
}

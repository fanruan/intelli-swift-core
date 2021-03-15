package com.fr.swift.cloud;

import com.fr.swift.cloud.basics.ProcessHandler;
import com.fr.swift.cloud.basics.exception.ClassIsNotInterfaceException;
import com.fr.swift.cloud.basics.exception.ProcessHandlerNotRegisterException;
import com.fr.swift.cloud.basics.exception.SwiftProxyException;
import junit.framework.TestCase;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class ProxyExceptionTest extends TestCase {

    public void testClassIsNotInterfaceException() {
        Exception e = new ClassIsNotInterfaceException(ProcessHandler.class);
        assertEquals(e.getMessage(), "ProcessHandler is not interface");
    }

    public void testSwiftProxyException() {
        Exception e = new SwiftProxyException("test exception");
        assertEquals(e.getMessage(), "test exception");
    }

    public void testProcessHandlerNotRegisterException() {
        Exception e = new ProcessHandlerNotRegisterException(ProcessHandler.class);
        assertEquals(e.getMessage(), "ProcessHandler is not register or process handler is null");
    }
}

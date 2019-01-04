package com.fr.swift;

import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.exception.ClassIsNotInterfaceException;
import com.fr.swift.basics.exception.ProcessHandlerNotRegisterException;
import com.fr.swift.basics.exception.SwiftProxyException;
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
        assertEquals(e.getMessage(), "com.fr.swift.basics.ProcessHandler is not interface");
    }

    public void testSwiftProxyException() {
        Exception e = new SwiftProxyException("test exception");
        assertEquals(e.getMessage(), "test exception");
    }

    public void testProcessHandlerNotRegisterException() {
        Exception e = new ProcessHandlerNotRegisterException(ProcessHandler.class);
        assertEquals(e.getMessage(), "com.fr.swift.basics.ProcessHandler is not register or process handler is null");
    }
}

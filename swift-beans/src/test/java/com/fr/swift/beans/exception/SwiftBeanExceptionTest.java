package com.fr.swift.beans.exception;

import junit.framework.TestCase;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftBeanExceptionTest extends TestCase {

    public void testSwiftBeanException1() {
        SwiftBeanException exception = new SwiftBeanException("testException");
        assertEquals(exception.getMessage(), "testException");
        assertNull(exception.getCause());
    }

    public void testSwiftBeanException2() {
        SwiftBeanException exception = new SwiftBeanException("testException", new Exception("testExceptionParam"));
        assertEquals(exception.getMessage(), "testException");
        assertNotNull(exception.getCause());
        assertEquals(exception.getCause().getMessage(), "testExceptionParam");

    }

    public void testSwiftBeanException3() {
        SwiftBeanException exception = new SwiftBeanException(new Exception("testExceptionParam"));
        assertEquals(exception.getMessage(), "java.lang.Exception: testExceptionParam");
        assertNotNull(exception.getCause());
        assertEquals(exception.getCause().getMessage(), "testExceptionParam");
    }

    public void testSwiftBeanExceptionWithType() {
        SwiftBeanException exception = new SwiftBeanException("testBeanName", SwiftBeanException.Type.EXIST);
        assertEquals(exception.getMessage(), "Swift bean name :testBeanName is EXIST");
        assertNull(exception.getCause());

        exception = new SwiftBeanException("testBeanName", SwiftBeanException.Type.NOT_EXIST);
        assertEquals(exception.getMessage(), "Swift bean name :testBeanName is NOT_EXIST");
        assertNull(exception.getCause());
    }
}

package com.fr.swift.beans.exception;

import junit.framework.TestCase;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class NoSuchBeanExceptionTest extends TestCase {

    public void testNoSuchBeanException() {
        NoSuchBeanException exception = new NoSuchBeanException("testBeanName");
        assertEquals("testBeanName's bean is not defined!", exception.getMessage());
        assertNull(exception.getCause());
    }
}

package com.fr.swift.beans.annotation.handle;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import junit.framework.TestCase;

/**
 * @author anner
 * @this class created on date 19-8-14
 * @description
 */
public class AnnotationHandlerContextTest extends TestCase {
    @Override
    protected void setUp() throws Exception {
        BeanFactory beanFactory= SwiftContext.get();
        beanFactory.registerPackages("com.fr.swift.beans.annotation.handle");
        beanFactory.init();
        super.setUp();
    }
    public void testProcess(){

    }
}

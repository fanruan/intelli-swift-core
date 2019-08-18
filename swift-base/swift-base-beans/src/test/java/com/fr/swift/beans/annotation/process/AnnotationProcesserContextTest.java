package com.fr.swift.beans.annotation.process;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.beans.factory.SwiftBeanDefinition;
import junit.framework.TestCase;

/**
 * @author anner
 * @this class created on date 2019/8/12
 * @description
 */
public class AnnotationProcesserContextTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        BeanFactory beanFactory= SwiftContext.get();
        beanFactory.registerPackages("com.fr.swift.beans.annotation.process");
        beanFactory.init();
        super.setUp();
    }

    public void testProcess(){
        SwiftBeanDefinition beanDefinition=new SwiftBeanDefinition(TestBean2.class,"test");
        AnnotationProcesserContext annotationProcesserContext=AnnotationProcesserContext.getInstance();
        annotationProcesserContext.process(beanDefinition);
        assertTrue(beanDefinition.getAutoWired());
        assertEquals(2, beanDefinition.getAutowiredFields().size());
        assertEquals("testInitMethod",beanDefinition.getInitMethod());
        assertEquals("testDestroyMethod",beanDefinition.getDestroyMethod());
    }
}

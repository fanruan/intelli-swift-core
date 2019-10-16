package com.fr.swift.beans.annotation.process;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.beans.factory.SwiftBeanRegistry;
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
        SwiftBeanDefinition beanDefinition1 = SwiftBeanRegistry.getInstance().getBeanDefinition("testBeanProcesser1");
        assertEquals(beanDefinition1.getBeanName(), "testBeanProcesser1");
        assertEquals(beanDefinition1.singleton(), true);
        assertEquals(beanDefinition1.getAutowiredFields().size(), 1);
        assertEquals(beanDefinition1.getInitMethod().getName(), "testInitMethod");
        assertEquals(beanDefinition1.getDestroyMethod().getName(), "testDestroyMethod");
        assertFalse(beanDefinition1.isAspect());
        assertNull(beanDefinition1.getBeforeMethod());
        assertNull(beanDefinition1.getAfterMethod());
        assertNull(beanDefinition1.getPointCut());
        assertEquals(beanDefinition1.getAdviceTarget().length, 0);

        SwiftBeanDefinition beanDefinition2 = SwiftBeanRegistry.getInstance().getBeanDefinition("testBeanProcesser2");
        assertEquals(beanDefinition2.getBeanName(), "testBeanProcesser2");
        assertFalse(beanDefinition2.singleton());
        assertEquals(beanDefinition2.getAutowiredFields().size(), 2);
        assertTrue(beanDefinition2.getAutowiredFields().containsValue("testBeanProcesser1"));
        assertTrue(beanDefinition2.getAutowiredFields().containsValue("testBeanProcesser4"));
        assertEquals(beanDefinition2.getInitMethod().getName(), "testInitMethod");
        assertEquals(beanDefinition2.getDestroyMethod().getName(), "testDestroyMethod");
        assertFalse(beanDefinition2.isAspect());
        assertNull(beanDefinition2.getBeforeMethod());
        assertNull(beanDefinition2.getAfterMethod());
        assertNull(beanDefinition2.getPointCut());
        assertEquals(beanDefinition2.getAdviceTarget().length, 0);

        SwiftBeanDefinition beanDefinition3 = SwiftBeanRegistry.getInstance().getBeanDefinition("testBeanProcesser3");
        assertEquals(beanDefinition3.getBeanName(), "testBeanProcesser3");
        assertTrue(beanDefinition3.singleton());
        assertEquals(beanDefinition3.getAutowiredFields().size(), 5);
        assertTrue(beanDefinition3.getAutowiredFields().containsValue("testBeanProcesser1"));
        assertTrue(beanDefinition3.getAutowiredFields().containsValue("testBeanProcesser4"));
        assertNull(beanDefinition3.getInitMethod());
        assertEquals(beanDefinition3.getDestroyMethod().getName(), "testDestroyMethod");
        assertFalse(beanDefinition3.isAspect());
        assertNull(beanDefinition3.getBeforeMethod());
        assertNull(beanDefinition3.getAfterMethod());
        assertNull(beanDefinition3.getPointCut());
        assertEquals(beanDefinition3.getAdviceTarget().length, 0);

        SwiftBeanDefinition beanDefinition4 = SwiftBeanRegistry.getInstance().getBeanDefinition("testBeanProcesser4");
        assertEquals(beanDefinition4.getBeanName(), "testBeanProcesser4");
        assertTrue(beanDefinition4.singleton());
        assertEquals(beanDefinition4.getAutowiredFields().size(), 0);
        assertNull(beanDefinition4.getInitMethod());
        assertNull(beanDefinition4.getDestroyMethod());
        assertTrue(beanDefinition4.isAspect());
        assertNotNull(beanDefinition4.getBeforeMethod());
        assertNotNull(beanDefinition4.getAfterMethod());
        assertNotNull(beanDefinition4.getPointCut());
        assertEquals(beanDefinition4.getAdviceTarget().length, 2);

    }
}

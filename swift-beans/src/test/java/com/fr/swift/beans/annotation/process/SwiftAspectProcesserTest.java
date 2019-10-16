package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.factory.SwiftBeanDefinition;
import junit.framework.TestCase;

/**
 * @author anner
 * @this class created on date 2019/8/20
 * @description
 */
public class SwiftAspectProcesserTest extends TestCase {
    public void testProcess() {
        SwiftBeanDefinition beanDefinition = new SwiftBeanDefinition(TestBeanProcesser4.class, "test");

        SwiftAspectProcesser aspectProcesser = new SwiftAspectProcesser();

        aspectProcesser.process(beanDefinition);

        assertTrue(beanDefinition.isAspect());
        assertEquals(beanDefinition.getBeforeMethod().getName(), "before");
        assertEquals(beanDefinition.getAfterMethod().getName(), "after");
        assertEquals(beanDefinition.getPointCut().getName(), "test");
        assertEquals(beanDefinition.getAdviceTarget()[0], "com.fr.swift.beans.annotation.process.TestBeanProcesser2.run");
        assertEquals(beanDefinition.getAdviceTarget()[1], "com.fr.swift.beans.annotation.process.TestBeanProcesser3.run");
    }
}

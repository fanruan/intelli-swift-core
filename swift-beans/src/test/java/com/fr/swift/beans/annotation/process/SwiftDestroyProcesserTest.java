package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.factory.SwiftBeanDefinition;
import junit.framework.TestCase;

/**
 * @author anner
 * @this class created on date 2019/8/12
 * @description
 */
public class SwiftDestroyProcesserTest extends TestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testProcess(){
        //存在destroy的情况
        SwiftBeanDefinition beanDefinition = new SwiftBeanDefinition(TestBeanProcesser1.class, "bean1");
        SwiftDestroyProcesser destroyProcesser=new SwiftDestroyProcesser();
        destroyProcesser.process(beanDefinition);
        assertEquals(beanDefinition.getDestroyMethod().getName(), "testDestroyMethod");
        //不存在destroy的情况
        beanDefinition = new SwiftBeanDefinition(TestBeanProcesser4.class, "testBeanProcesser4");
        destroyProcesser.process(beanDefinition);
        assertNull(beanDefinition.getDestroyMethod());
    }
}

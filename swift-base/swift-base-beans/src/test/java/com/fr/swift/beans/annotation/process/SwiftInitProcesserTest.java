package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.factory.SwiftBeanDefinition;
import junit.framework.TestCase;

/**
 * @author anner
 * @this class created on date 2019/8/12
 * @description
 */
public class SwiftInitProcesserTest extends TestCase {
    public void testProcess(){
        //存在init的情况
        SwiftBeanDefinition beanDefinition=new SwiftBeanDefinition(TestBean1.class,"bean1");
        SwiftInitProcesser initProcesser=new SwiftInitProcesser();
        initProcesser.process(beanDefinition);
        assertEquals(beanDefinition.getInitMethod().getName(), "testInitMethod");
        //不存在init的情况
        beanDefinition=new SwiftBeanDefinition(TestBean3.class,"testBean3");
        initProcesser.process(beanDefinition);
        assertNull(beanDefinition.getInitMethod());
    }
}

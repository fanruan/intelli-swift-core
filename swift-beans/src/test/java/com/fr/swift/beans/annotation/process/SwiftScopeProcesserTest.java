package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.factory.SwiftBeanDefinition;
import junit.framework.TestCase;

/**
 * @author anner
 * @this class created on date 2019/8/12
 * @description
 */
public class SwiftScopeProcesserTest extends TestCase {

    public void testProcess(){
        //测试scope不存在
        SwiftBeanDefinition beanDefinition = new SwiftBeanDefinition(TestBeanProcesser4.class, "testBeanProcesser4");
        SwiftScopeProcesser scopeProcesser=new SwiftScopeProcesser();
        scopeProcesser.process(beanDefinition);
        assertTrue(beanDefinition.singleton());
        //测试scope存在且是单例
        beanDefinition = new SwiftBeanDefinition(TestBeanProcesser1.class, "testBeanProcesser1");
        scopeProcesser.process(beanDefinition);
        assertTrue(beanDefinition.singleton());
        //测试scope是多例
        beanDefinition = new SwiftBeanDefinition(TestBeanProcesser2.class, "testBeanProcesser2");
        scopeProcesser.process(beanDefinition);
        assertFalse(beanDefinition.singleton());
    }
}

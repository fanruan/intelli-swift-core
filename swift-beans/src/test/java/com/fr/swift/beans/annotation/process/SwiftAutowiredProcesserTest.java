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
public class SwiftAutowiredProcesserTest extends TestCase {
    @Override
    protected void setUp() throws Exception {
        BeanFactory beanFactory = SwiftContext.get();
        beanFactory.registerPackages("com.fr.swift.beans.annotation.process");
        beanFactory.init();
        super.setUp();
    }

    public void testProcess() throws NoSuchFieldException {
        //测试没有属性的情况
        SwiftBeanDefinition beanDefinition = new SwiftBeanDefinition(TestBeanProcesser4.class, "test");
        SwiftAutowiredProcesser autowiredProcesser = new SwiftAutowiredProcesser();
        autowiredProcesser.process(beanDefinition);
        assertEquals(0, beanDefinition.getAutowiredFields().size());
        assertFalse(beanDefinition.getAutoWired());
        //测试只有自己的autowired
        beanDefinition = new SwiftBeanDefinition(TestBeanProcesser1.class, "test");
        autowiredProcesser.process(beanDefinition);
        assertEquals(1, beanDefinition.getAutowiredFields().size());
        //测试继承关系的autowired
        beanDefinition = new SwiftBeanDefinition(TestBeanProcesser3.class, "test");
        autowiredProcesser.process(beanDefinition);
        assertEquals(5, beanDefinition.getAutowiredFields().size());
    }
}

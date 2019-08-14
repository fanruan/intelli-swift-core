package com.fr.swift.beans.annotation.process;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.exception.NoSuchBeanException;
import com.fr.swift.beans.exception.SwiftBeanException;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.beans.factory.SwiftBeanRegistry;
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

    public void testProcess() {
        //测试没有属性的情况
        SwiftBeanDefinition beanDefinition = new SwiftBeanDefinition(TestBean1.class, "test");
        SwiftAutowiredProcesser autowiredProcesser = new SwiftAutowiredProcesser();
        autowiredProcesser.process(beanDefinition);
        assertEquals(0, beanDefinition.getAutowiredClassList().size());
        assertFalse(beanDefinition.getAutoWired());
        //测试没有被托管
        beanDefinition = new SwiftBeanDefinition(TestBean4.class, "TestBean4");
        try {
            autowiredProcesser.process(beanDefinition);
        } catch (Exception e) {
            assertEquals(NoSuchBeanException.class, e.getClass());
        }
        //测试存在多个且没有qualilifer
        SwiftBeanRegistry.getInstance().registerBeanNamesByType(TestBean1.class, "bean1");
        assertEquals(2, SwiftBeanRegistry.getInstance().getBeanNamesByType(TestBean1.class).size());
        beanDefinition = new SwiftBeanDefinition(TestBean2.class, "bean");
        try {
            autowiredProcesser.process(beanDefinition);
        } catch (Exception e) {
            assertEquals(SwiftBeanException.class, e.getClass());
        }
        //测试存在多个但是存在qualilifer
        assertEquals(2, SwiftBeanRegistry.getInstance().getBeanNamesByType(TestBean1.class).size());
        beanDefinition=new SwiftBeanDefinition(TestBean3.class,"testBean");
        autowiredProcesser.process(beanDefinition);
        assertEquals(2,beanDefinition.getAutowiredClassList().size());
    }
}

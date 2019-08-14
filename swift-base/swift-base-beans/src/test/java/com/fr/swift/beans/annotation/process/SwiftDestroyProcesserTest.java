package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.beans.factory.SwiftBeanRegistry;
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
        SwiftBeanDefinition beanDefinition=new SwiftBeanDefinition(TestBean1.class,"bean1");
        SwiftDestroyProcesser destroyProcesser=new SwiftDestroyProcesser();
        destroyProcesser.process(beanDefinition);
        assertEquals(beanDefinition.getDestroyMethod(),"testDestroyMethod");
        //不存在destroy的情况
        beanDefinition=new SwiftBeanDefinition(TestBean4.class,"testBean4");
        destroyProcesser.process(beanDefinition);
        assertEquals(beanDefinition.getDestroyMethod(),"");
    }
}

package com.fr.swift.beans.factory;

import com.fr.swift.beans.exception.SwiftBeanException;
import com.fr.swift.beans.factory.bean.IBean;
import com.fr.swift.beans.factory.bean.ITestWithoutBeanPrototype;
import com.fr.swift.beans.factory.bean.ITestWithoutBeanSingleton;
import com.fr.swift.beans.factory.bean.TestWithoutBeanPrototype;
import com.fr.swift.beans.factory.bean.TestWithoutBeanSingleton;
import junit.framework.TestCase;

import java.util.Set;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class BeanRegistryTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testEditBeanDefinition() {
        BeanRegistry beanRegistry = new AbstractBeanRegistry() {
        };
        assertEquals(beanRegistry.getBeanDefinitionMap().size(), 0);
        beanRegistry.registerBeanDefinition("bean1", new SwiftBeanDefinition(ITestWithoutBeanSingleton.class, "bean1"));
        beanRegistry.registerBeanDefinition("bean2", new SwiftBeanDefinition(ITestWithoutBeanPrototype.class, "bean2", "prototype"));
        assertEquals(beanRegistry.getBeanDefinitionMap().size(), 2);
        try {
            beanRegistry.registerBeanDefinition("bean1", new SwiftBeanDefinition(ITestWithoutBeanSingleton.class, "bean1"));
            assertTrue(false);
        } catch (SwiftBeanException e) {
            assertTrue(true);
        }
        assertEquals(beanRegistry.getBeanDefinition("bean1").getClazz(), ITestWithoutBeanSingleton.class);
        assertTrue(beanRegistry.getBeanDefinition("bean1").singleton());
        assertEquals(beanRegistry.getBeanDefinition("bean1").getBeanName(), "bean1");

        assertEquals(beanRegistry.getBeanDefinition("bean2").getClazz(), ITestWithoutBeanPrototype.class);
        assertFalse(beanRegistry.getBeanDefinition("bean2").singleton());
        assertEquals(beanRegistry.getBeanDefinition("bean2").getBeanName(), "bean2");

        assertTrue(beanRegistry.containsBeanDefinition("bean1"));
        beanRegistry.removeBeanDefinition("bean1");
        assertFalse(beanRegistry.containsBeanDefinition("bean1"));
    }

    public void testEditBeanNamesByType() {
        BeanRegistry beanRegistry = new AbstractBeanRegistry() {
        };
        Set<Class<?>> interfaces = SwiftClassUtil.getAllInterfacesAndSelf(TestWithoutBeanSingleton.class);
        for (Class<?> anInterface : interfaces) {
            beanRegistry.registerBeanNamesByType(anInterface, "testWithoutBeanSingleton");
        }

        interfaces = SwiftClassUtil.getAllInterfacesAndSelf(TestWithoutBeanPrototype.class);
        for (Class<?> anInterface : interfaces) {
            beanRegistry.registerBeanNamesByType(anInterface, "testWithoutBeanPrototype");
        }

        assertEquals(beanRegistry.getBeanNamesByType(TestWithoutBeanSingleton.class).get(0), "testWithoutBeanSingleton");
        assertEquals(beanRegistry.getBeanNamesByType(ITestWithoutBeanSingleton.class).size(), 1);
        assertEquals(beanRegistry.getBeanNamesByType(ITestWithoutBeanSingleton.class).get(0), "testWithoutBeanSingleton");

        assertEquals(beanRegistry.getBeanNamesByType(TestWithoutBeanPrototype.class).size(), 1);
        assertEquals(beanRegistry.getBeanNamesByType(TestWithoutBeanPrototype.class).get(0), "testWithoutBeanPrototype");
        assertEquals(beanRegistry.getBeanNamesByType(ITestWithoutBeanPrototype.class).size(), 1);
        assertEquals(beanRegistry.getBeanNamesByType(ITestWithoutBeanPrototype.class).get(0), "testWithoutBeanPrototype");

        assertEquals(beanRegistry.getBeanNamesByType(IBean.class).size(), 2);
        assertEquals(beanRegistry.getBeanNamesByType(IBean.class).get(0), "testWithoutBeanSingleton");
        assertEquals(beanRegistry.getBeanNamesByType(IBean.class).get(1), "testWithoutBeanPrototype");

        beanRegistry.removeBeanNamesByType(IBean.class, "testWithoutBeanPrototype");
        assertEquals(beanRegistry.getBeanNamesByType(IBean.class).size(), 1);
        assertEquals(beanRegistry.getBeanNamesByType(IBean.class).get(0), "testWithoutBeanSingleton");
    }
}

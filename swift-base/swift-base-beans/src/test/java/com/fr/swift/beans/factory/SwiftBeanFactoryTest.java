package com.fr.swift.beans.factory;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.exception.SwiftBeanException;
import com.fr.swift.beans.factory.bean.BeanWithMethod1;
import com.fr.swift.beans.factory.bean.BeanWithMethod2;
import com.fr.swift.beans.factory.bean.IBean;
import com.fr.swift.beans.factory.bean.ITestWithoutBeanPrototype;
import com.fr.swift.beans.factory.bean.ITestWithoutBeanSingleton;
import com.fr.swift.beans.factory.bean.TestWithoutBeanPrototype;
import com.fr.swift.beans.factory.bean.TestWithoutBeanSingleton;
import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/11/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftBeanFactoryTest extends TestCase {

    private BeanFactory beanFactory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        beanFactory = new SwiftBeanFactory();
        beanFactory.registerPackages("com.fr.swift.beans.factory.bean");
        beanFactory.init();
    }

    public void testGetBeanByNameClassSingleton() {
        TestWithoutBeanSingleton bean1 = beanFactory.getBean("testWithoutBeanSingleton", TestWithoutBeanSingleton.class);
        ITestWithoutBeanSingleton bean2 = beanFactory.getBean("testWithoutBeanSingleton", ITestWithoutBeanSingleton.class);
        IBean bean3 = beanFactory.getBean("testWithoutBeanSingleton", IBean.class);

        assertEquals(bean1, bean2);
        assertEquals(bean1, bean3);
    }

    public void testGetBeanByClassSingleton() {
        TestWithoutBeanSingleton bean1 = beanFactory.getBean(TestWithoutBeanSingleton.class);
        ITestWithoutBeanSingleton bean2 = beanFactory.getBean(ITestWithoutBeanSingleton.class);
        try {
            IBean bean3 = beanFactory.getBean(IBean.class);
            assertTrue(false);
        } catch (SwiftBeanException swiftBeanException) {
            assertEquals(swiftBeanException.getMessage(), "com.fr.swift.beans.factory.bean.IBean 's beanNames size >= 2");
        }
        assertEquals(bean1, bean2);
    }

    public void testGetBeanByNameSingleton() {
        try {
            TestWithoutBeanSingleton bean1 = (TestWithoutBeanSingleton) beanFactory.getBean("testWithoutBeanSingleton");
            assertNotNull(bean1);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testGetBeanByNameClassPrototype() {
        TestWithoutBeanPrototype bean1 = beanFactory.getBean("test_custom", TestWithoutBeanPrototype.class);
        TestWithoutBeanPrototype bean2 = beanFactory.getBean("test_custom", TestWithoutBeanPrototype.class);
        TestWithoutBeanPrototype bean3 = beanFactory.getBean("test_custom", TestWithoutBeanPrototype.class);
        assertNotSame(bean1, bean2);
        assertNotSame(bean2, bean3);
        assertNotSame(bean1, bean3);
        assertTrue(bean1 instanceof TestWithoutBeanPrototype);
        assertTrue(bean2 instanceof TestWithoutBeanPrototype);
        assertTrue(bean3 instanceof TestWithoutBeanPrototype);
        try {
            ITestWithoutBeanPrototype bean4 = beanFactory.getBean("test_custom", ITestWithoutBeanPrototype.class);
            assertNotNull(bean4);
        } catch (SwiftBeanException e) {
            assertTrue(false);
        }
        try {
            IBean bean5 = beanFactory.getBean("test_custom", IBean.class);
            assertNotNull(bean5);
        } catch (SwiftBeanException e) {
            assertTrue(false);
        }
    }

    public void testGetBeanByClassPrototype() {
        TestWithoutBeanPrototype bean1 = beanFactory.getBean(TestWithoutBeanPrototype.class);
        TestWithoutBeanPrototype bean2 = beanFactory.getBean(TestWithoutBeanPrototype.class);
        TestWithoutBeanPrototype bean3 = beanFactory.getBean(TestWithoutBeanPrototype.class);
        assertNotSame(bean1, bean2);
        assertNotSame(bean2, bean3);
        assertNotSame(bean1, bean3);
        assertTrue(bean1 instanceof TestWithoutBeanPrototype);
        assertTrue(bean2 instanceof TestWithoutBeanPrototype);
        assertTrue(bean3 instanceof TestWithoutBeanPrototype);
        try {
            ITestWithoutBeanPrototype bean4 = beanFactory.getBean(ITestWithoutBeanPrototype.class);
            assertNotNull(false);
        } catch (SwiftBeanException e) {
            assertTrue(false);
        }
        try {
            IBean bean5 = beanFactory.getBean(IBean.class);
            assertTrue(false);
        } catch (SwiftBeanException e) {
            assertTrue(true);
        }
    }

    public void testGetBeanByNamePrototype() {
        TestWithoutBeanPrototype bean1 = (TestWithoutBeanPrototype) beanFactory.getBean("test_custom");
        TestWithoutBeanPrototype bean2 = (TestWithoutBeanPrototype) beanFactory.getBean("test_custom");
        TestWithoutBeanPrototype bean3 = (TestWithoutBeanPrototype) beanFactory.getBean("test_custom");

        ITestWithoutBeanPrototype bean4 = (ITestWithoutBeanPrototype) beanFactory.getBean("test_custom");
        IBean bean5 = (IBean) beanFactory.getBean("test_custom");

        assertNotSame(bean1, bean2);
        assertNotSame(bean2, bean3);
        assertNotSame(bean1, bean3);
        assertNotSame(bean4, bean5);
        assertNotSame(bean1, bean4);
        assertNotSame(bean1, bean5);

        assertTrue(bean1 instanceof TestWithoutBeanPrototype);
        assertTrue(bean2 instanceof TestWithoutBeanPrototype);
        assertTrue(bean3 instanceof TestWithoutBeanPrototype);
        assertTrue(bean4 instanceof TestWithoutBeanPrototype);
        assertTrue(bean5 instanceof TestWithoutBeanPrototype);
    }

    public void testIsSingleton() {
        assertTrue(beanFactory.isSingleton("testWithoutBeanSingleton"));
        assertFalse(beanFactory.isSingleton("test_custom"));
    }

    public void testGetType() {
        assertEquals(beanFactory.getType("testWithoutBeanSingleton"), TestWithoutBeanSingleton.class);
        assertEquals(beanFactory.getType("test_custom"), TestWithoutBeanPrototype.class);
    }

    public void testIsTypeMatch() {
        assertTrue(beanFactory.isTypeMatch("testWithoutBeanSingleton", TestWithoutBeanSingleton.class));
        assertTrue(beanFactory.isTypeMatch("testWithoutBeanSingleton", ITestWithoutBeanSingleton.class));
        assertTrue(beanFactory.isTypeMatch("testWithoutBeanSingleton", IBean.class));
    }

    public void testGetBeansByAnnotations() {
        IBean bean = (IBean) beanFactory.getBean("testWithoutBeanSingleton");
        Map<String, Object> map = beanFactory.getBeansByAnnotations(SwiftBean.class);
        assertEquals(map.size(), 1);
        assertEquals(map.get("testWithoutBeanSingleton"), bean);
    }

    public void testgetClassesByAnnotations() {
        List<Class<?>> list = beanFactory.getClassesByAnnotations(SwiftBean.class);
        assertEquals(list.size(), 2);
        assertEquals(list.get(0), TestWithoutBeanPrototype.class);
        assertEquals(list.get(1), TestWithoutBeanSingleton.class);
    }

    //测试refresh
    public void  testRefresh() throws InvocationTargetException, IllegalAccessException {
        SwiftContext.get().refresh(BeanWithMethod1.class);
        assertEquals(10,SwiftContext.get().getBean(BeanWithMethod1.class).number);
    }

    //测试reFreshAll
    public void testRefreshAll() throws InvocationTargetException, IllegalAccessException {
        SwiftContext.get().refreshAll();
        assertEquals(10,SwiftContext.get().getBean(BeanWithMethod1.class).number);
        assertEquals(10,SwiftContext.get().getBean(BeanWithMethod2.class).number);
    }
}
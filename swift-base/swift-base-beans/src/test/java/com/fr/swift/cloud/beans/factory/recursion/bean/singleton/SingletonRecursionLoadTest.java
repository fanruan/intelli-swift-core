package com.fr.swift.cloud.beans.factory.recursion.bean.singleton;

import junit.framework.TestCase;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SingletonRecursionLoadTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        TestBeanFactory.getInstance().registerPackages("com.fr.swift.cloud.beans.factory.recursion.bean.singleton");
        TestBeanFactory.getInstance().init();
    }

    /**
     * D has B,C
     * C has A
     * B has A
     * ABCD->singleton
     */
    public void testRecursionLoadSingleton() {
        RecursionA a = TestBeanFactory.getInstance().getBean(RecursionA.class);
        RecursionB b = TestBeanFactory.getInstance().getBean(RecursionB.class);
        RecursionC c = TestBeanFactory.getInstance().getBean(RecursionC.class);
        RecursionD d = TestBeanFactory.getInstance().getBean(RecursionD.class);
        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(c);
        assertNotNull(d);
        assertEquals(c, d.recursionC);
        assertEquals(b, d.recursionB);
        assertEquals(a, d.recursionB.recursionA);
        assertEquals(a, d.recursionC.recursionA);
        assertEquals(a, b.recursionA);
        assertEquals(a, c.recursionA);
    }
}

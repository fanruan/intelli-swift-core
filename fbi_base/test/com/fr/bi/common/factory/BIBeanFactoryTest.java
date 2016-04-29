package com.fr.bi.common.factory;

import com.fr.bi.stable.utils.code.BILogger;

import junit.framework.TestCase;

/**
 * Created by Connery on 2015/12/7.
 */
public class BIBeanFactoryTest extends TestCase {
    public void testSingleton() {
        BIBeanFactory factory = new BIBeanFactory();
        try {
            factory.registerClass("bean", BITestBean.class);
            factory.registerClass("sbean", BISingletonTestBean.class);
            BISingletonTestBean bean1 = (BISingletonTestBean) factory.getObject("sbean", "1");
            BISingletonTestBean bean2 = (BISingletonTestBean) factory.getObject("sbean", "2");
            assertEquals(bean1.name, "1");
            assertEquals(bean2.name, "1");
            assertEquals(bean2, bean1);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testSingletonNullPara() {
        BIBeanFactory factory = new BIBeanFactory();
        try {

            factory.registerClass("bean", BITestBean.class);
            factory.registerClass("sbean", BISingletonTestBean.class);
            BISingletonTestBean bean2 = (BISingletonTestBean) factory.getObject("sbean", "2");
            BISingletonTestBean bean1 = (BISingletonTestBean) factory.getObject("sbean");

            BISingletonTestBean bean3 = (BISingletonTestBean) factory.getObject("sbean", "3");

            assertEquals(bean1.name, "2");
            assertEquals(bean2.name, "2");
            assertNotSame(bean3.name, "3");

            assertEquals(bean2, bean1);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testNonSingleton() {
        BIBeanFactory factory = new BIBeanFactory();
        try {

            factory.registerClass("bean", BITestBean.class);
            factory.registerClass("sbean", BISingletonTestBean.class);
            BITestBean bean1 = (BITestBean) factory.getObject("bean", "1");
            BITestBean bean2 = (BITestBean) factory.getObject("bean", "2");
            assertEquals(bean1.name, "1");
            assertEquals(bean2.name, "2");
            assertNotSame(bean1, bean2);

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testNonSingletonTwoParameter() {
        BIBeanFactory factory = new BIBeanFactory();
        try {

            factory.registerClass("bean", BITestBean.class);
            factory.registerClass("sbean", BISingletonTestBean.class);
            BITestBean bean1 = (BITestBean) factory.getObject("bean", 1l, 3l);
            BITestBean bean2 = (BITestBean) factory.getObject("bean", 2l, 4l);
            assertEquals(bean1.age, 3l);
            assertEquals(bean2.age, 4l);
            assertEquals(bean1.objAge, Long.valueOf(1l));
            assertEquals(bean2.objAge, Long.valueOf(2l));

            assertNotSame(bean1, bean2);

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testNonSingletonNoneParameter() {
        BIBeanFactory factory = new BIBeanFactory();
        try {
            factory.registerClass("bean", BITestBean.class);
            factory.registerClass("sbean", BISingletonTestBean.class);
            BITestBean bean1 = (BITestBean) factory.getObject("bean", new Object[]{});
            BITestBean bean2 = (BITestBean) factory.getObject("bean", new Object[]{});
            assertEquals(bean1.age, -2);
            assertEquals(bean2.age, -2);
            assertNotSame(bean1, bean2);

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testNonSingletonNullParameter() {
        BIBeanFactory factory = new BIBeanFactory();
        try {

            factory.registerClass("bean", BITestBean.class);
            factory.registerClass("sbean", BISingletonTestBean.class);
            BITestBean bean1 = (BITestBean) factory.getObject("bean", 1l);
            BITestBean bean2 = (BITestBean) factory.getObject("bean", 2l);
            assertEquals(bean1.objAge, Long.valueOf(1));
            assertEquals(bean2.objAge, Long.valueOf(2));
            assertNotSame(bean1, bean2);

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }


}
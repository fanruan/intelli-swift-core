package com.fr.bi.common.factory;

import com.fr.bi.stable.utils.code.BILogger;

import junit.framework.TestCase;

/**
 * Created by Connery on 2015/12/8.
 */
public class BIConfFactoryTest extends TestCase {
    public void testNonSingletonHelper() {

        try {
            BIFactory.getInstance().registerClass(IModuleFactory.CONF_MODULE, BITestBean.class.getName(), BITestBean.class);

            long a = 1;
            long b = 2;
            BITestBean bean1 = BIFactoryHelper.getObject(BITestBean.class, a);
            BITestBean bean2 = BIFactoryHelper.getObject(BITestBean.class, b);
            assertEquals(bean1.objAge, Long.valueOf(a));
            assertEquals(bean2.objAge, Long.valueOf(b));
            assertNotSame(bean1, bean2);

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}
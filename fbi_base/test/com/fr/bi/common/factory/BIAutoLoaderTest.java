package com.fr.bi.common.factory;

import junit.framework.TestCase;

/**
 * Created by Connery on 2015/12/8.
 */
public class BIAutoLoaderTest extends TestCase {
    public void testLoader() {
        BIRegister4TestBasic basic = BIFactoryHelper.getObject(BIRegister4TestBasic.class);
        BIRegister4TestBasic one = BIFactoryHelper.getObject(BIRegister4TestBasic.class);
        assertEquals(one.getClass(), BIRegister4TestBasic.class);
        BIFactory.getInstance().changeModuleFactory(IFactoryService.CONF_DB);
        BIRegister4TestBasic two = BIFactoryHelper.getObject(BIRegister4TestBasic.class);
        assertEquals(two.getClass(), BIRegister4TestTwo.class);

    }
}
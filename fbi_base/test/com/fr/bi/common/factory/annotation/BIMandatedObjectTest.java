package com.fr.bi.common.factory.annotation;

import com.fr.bi.common.factory.IFactoryService;

import junit.framework.TestCase;

/**
 * Created by Connery on 2015/12/8.
 */
public class BIMandatedObjectTest extends TestCase {

    public void testAnnotation() {
        BIMandatedObject o = BIRegisterObject4test.class.getAnnotation(BIMandatedObject.class);
        assertEquals(o.factory(), IFactoryService.CONF_XML);
        assertEquals(o.key(), "key");
    }


    public void testInterfaceAnnotation() {
        Class interaceImp = InterfaceAnnImp.class;

        System.out.println("");
    }
}
package com.fr.bi.stable.utils.program;

import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.common.factory.annotation.BIRegisterObject4test;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Connery on 2015/12/8.
 */
public class BIClassUtilsTest extends TestCase {

    public void testGetClass() {
        BIClassUtils.getClasses("com.fr.bi");
    }

    public void testFindTestClass() {
        Iterator<Class<?>> it = BIClassUtils.getClasses("com.fr.bi").iterator();
        Set<String> classNames = new HashSet<String>();
        while (it.hasNext()) {
            Class clazz = it.next();
            if (clazz.isAnnotationPresent(BIMandatedObject.class)) {
                classNames.add(clazz.getName());
            }
        }
        assertTrue(classNames.contains(BIRegisterObject4test.class.getName()));
    }
}
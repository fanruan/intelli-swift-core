package com.fr.swift.beans.factory;

import com.fr.swift.beans.factory.bean.IBean;
import com.fr.swift.beans.factory.bean.ITestWithBean;
import com.fr.swift.beans.factory.bean.TestWithBean;
import junit.framework.TestCase;

import java.util.Set;

/**
 * This class created on 2018/11/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClassUtilTest extends TestCase {

    public void testGetAllInterfacesAndSelf() {
        Set<Class<?>> interfaces = SwiftClassUtil.getAllInterfacesAndSelf(TestWithBean.class);
        assertEquals(interfaces.size(), 3);
        for (Class<?> anInterface : interfaces) {
            assertTrue(anInterface.equals(ITestWithBean.class)
                    || anInterface.equals(IBean.class)
                    || anInterface.equals(TestWithBean.class));
        }
    }

    public void testShortName() {
        String defaultBeanName = SwiftClassUtil.getDefaultBeanName(TestWithBean.class.getName());
        assertEquals(defaultBeanName, "testWithBean");
    }
}

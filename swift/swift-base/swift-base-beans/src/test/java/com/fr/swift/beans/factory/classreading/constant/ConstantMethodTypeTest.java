package com.fr.swift.beans.factory.classreading.constant;

import com.fr.swift.beans.factory.classreading.ConstantInfo;
import com.fr.swift.beans.factory.classreading.TestInputStream;
import junit.framework.TestCase;

import java.io.InputStream;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ConstantMethodTypeTest extends TestCase {
    public void testConstantMethodType() {
        byte[] bytes = {0, 20};
        InputStream inputStream = new TestInputStream(bytes);
        ConstantMethodType constantFloat = (ConstantMethodType) ConstantInfo.getConstantInfo((short) 16);
        constantFloat.read(inputStream);
        assertEquals(constantFloat.getDescType(), 20);
    }
}

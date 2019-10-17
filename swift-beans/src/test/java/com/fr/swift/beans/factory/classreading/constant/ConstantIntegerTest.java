package com.fr.swift.beans.factory.classreading.constant;

import com.fr.swift.beans.factory.classreading.ConstantInfo;
import com.fr.swift.beans.factory.classreading.TestInputStream;
import junit.framework.TestCase;

import java.io.InputStream;

/**
 * This class created on 2018/12/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ConstantIntegerTest extends TestCase {

    public void testConstantInteger() {
        byte[] bytes = {0, 0, 0, 20};
        InputStream inputStream = new TestInputStream(bytes);
        ConstantInteger constantFloat = (ConstantInteger) ConstantInfo.getConstantInfo((short) 3);
        constantFloat.read(inputStream);
        assertEquals(constantFloat.getValue(), 20);
    }
}

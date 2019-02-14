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
public class ConstantStringTest extends TestCase {
    public void testConstantString() {
        byte[] bytes = {0, 10};
        InputStream inputStream = new TestInputStream(bytes);
        ConstantString constantFloat = (ConstantString) ConstantInfo.getConstantInfo((short) 8);
        constantFloat.read(inputStream);
        assertEquals(constantFloat.getNameIndex(), 10);
    }
}

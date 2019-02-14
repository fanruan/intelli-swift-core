package com.fr.swift.beans.factory.classreading.constant;

import com.fr.swift.beans.factory.classreading.ConstantInfo;
import com.fr.swift.beans.factory.classreading.TestInputStream;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class created on 2018/12/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ConstantFloatTest extends TestCase {
    public void testConstantFloat() throws IOException {
        byte[] bytes = {0, 0, 0, 10};
        InputStream inputStream = new TestInputStream(bytes);
        ConstantFloat constantFloat = (ConstantFloat) ConstantInfo.getConstantInfo((short) 4);
        constantFloat.read(inputStream);
        assertEquals(constantFloat.getValue(), 10);
    }
}

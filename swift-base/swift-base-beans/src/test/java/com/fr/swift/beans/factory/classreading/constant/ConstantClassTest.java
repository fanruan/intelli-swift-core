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
public class ConstantClassTest extends TestCase {

    public void testConstantClass() {
        byte[] bytes = {0, 10};
        InputStream inputStream = new TestInputStream(bytes);
        ConstantClass constantFloat = (ConstantClass) ConstantInfo.getConstantInfo((short) 7);
        constantFloat.read(inputStream);
        assertEquals(constantFloat.getNameIndex(), 10);
    }
}

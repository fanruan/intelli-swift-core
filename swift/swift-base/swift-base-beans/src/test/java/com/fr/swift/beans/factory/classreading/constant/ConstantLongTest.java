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
public class ConstantLongTest extends TestCase {
    public void testConstantLong() {
        byte[] bytes = {0, 0, 0, 10, 0, 0, 0, 20};
        InputStream inputStream = new TestInputStream(bytes);
        ConstantLong constantFloat = (ConstantLong) ConstantInfo.getConstantInfo((short) 5);
        constantFloat.read(inputStream);
        assertEquals(constantFloat.getHighValue(), 10);
        assertEquals(constantFloat.getLowValue(), 20);
    }
}

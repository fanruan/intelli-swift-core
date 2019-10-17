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
public class ConstantDoubleTest extends TestCase {

    public void testConstantDouble() {
        byte[] bytes = {0, 0, 0, 10, 0, 0, 0, 11};
        InputStream inputStream = new TestInputStream(bytes);
        ConstantDouble constantFloat = (ConstantDouble) ConstantInfo.getConstantInfo((short) 6);
        constantFloat.read(inputStream);
        assertEquals(constantFloat.getHighValue(), 10);
        assertEquals(constantFloat.getLowValue(), 11);
    }
}

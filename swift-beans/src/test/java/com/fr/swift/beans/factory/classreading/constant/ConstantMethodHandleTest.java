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
public class ConstantMethodHandleTest extends TestCase {
    public void testConstantMethodHandle() {
        byte[] bytes = {10, 0, 20};
        InputStream inputStream = new TestInputStream(bytes);
        ConstantMethodHandle constantFloat = (ConstantMethodHandle) ConstantInfo.getConstantInfo((short) 15);
        constantFloat.read(inputStream);
        assertEquals(constantFloat.getReferenceKind(), 10);
        assertEquals(constantFloat.getReferenceIndex(), 20);
    }
}

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
public class ConstantUtf8Test extends TestCase {
    public void testConstantUtf8() {
        String str = "ConstantUtf8Test";
        byte[] strBytes = str.getBytes();
        int length = strBytes.length;
        byte[] bytes = new byte[length + 2];
        bytes[0] = 0;
        bytes[1] = (byte) length;
        System.arraycopy(strBytes, 0, bytes, 2, strBytes.length);
        InputStream inputStream = new TestInputStream(bytes);
        ConstantUtf8 constantFloat = (ConstantUtf8) ConstantInfo.getConstantInfo((short) 1);
        constantFloat.read(inputStream);
        assertEquals(constantFloat.getValue(), "ConstantUtf8Test");
    }
}

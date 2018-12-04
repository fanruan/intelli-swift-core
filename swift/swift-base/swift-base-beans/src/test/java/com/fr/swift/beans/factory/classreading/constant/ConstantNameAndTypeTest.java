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
public class ConstantNameAndTypeTest extends TestCase {
    public void testonstantNameAndType() {
        byte[] bytes = {0, 10, 0, 20};
        InputStream inputStream = new TestInputStream(bytes);
        ConstantNameAndType constantFloat = (ConstantNameAndType) ConstantInfo.getConstantInfo((short) 12);
        constantFloat.read(inputStream);
        assertEquals(constantFloat.getNameIndex(), 10);
        assertEquals(constantFloat.getDescIndex(), 20);
    }
}

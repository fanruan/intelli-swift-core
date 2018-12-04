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
public class ConstantInvokeDynamicTest extends TestCase {

    public void testConstantInvokeDynamic() {
        byte[] bytes = {0, 10, 0, 20};
        InputStream inputStream = new TestInputStream(bytes);
        ConstantInvokeDynamic constantFloat = (ConstantInvokeDynamic) ConstantInfo.getConstantInfo((short) 18);
        constantFloat.read(inputStream);
        assertEquals(constantFloat.getBootstrapMethodAttrIndex(), 10);
        assertEquals(constantFloat.getNameAndTypeIndex(), 20);
    }
}

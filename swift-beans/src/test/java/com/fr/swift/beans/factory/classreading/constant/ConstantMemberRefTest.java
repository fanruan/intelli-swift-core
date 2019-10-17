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
public class ConstantMemberRefTest extends TestCase {
    public void testConstantMemberRef() {
        byte[] bytes = {0, 10, 0, 20};
        InputStream inputStream = new TestInputStream(bytes);
        ConstantMemberRef constantFloat = (ConstantMemberRef) ConstantInfo.getConstantInfo((short) 9);
        constantFloat.read(inputStream);
        assertEquals(constantFloat.getClassIndex(), 10);
        assertEquals(constantFloat.getNameAndTypeIndex(), 20);
    }
}

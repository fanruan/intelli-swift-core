package com.fr.swift.beans.factory.classreading;

import com.fr.swift.beans.factory.classreading.basic.MemberInfo;
import junit.framework.Test;
import junit.framework.TestCase;

/**
 * This class created on 2018/12/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClassFileTest extends TestCase {

    public void testClassFile() {
        ClassFile classFile = new ClassFile();
        classFile.setClassName(ClassFileTest.class.getName());
        classFile.setAccessFlag(33);
        classFile.setConstantPoolCount(10);
        classFile.setConstantPool(new ConstantPool(10));
        classFile.setMajorVersion(100);
        classFile.setMinorVersion(102);
        classFile.setMagic(1000);
        classFile.setFieldCount(20);
        classFile.setFields(new MemberInfo[0]);
        classFile.setInterfaceCount(1);
        classFile.setInterfaces(new String[]{Test.class.getName()});
        classFile.setSuperClass(TestCase.class.getName());
        classFile.setMethodCount(1);
        classFile.setMethods(new MemberInfo[1]);

        assertEquals(classFile.getClassName(), ClassFileTest.class.getName());
        assertEquals(classFile.getAccessFlag(), 33);
        assertEquals(classFile.getConstantPoolCount(), 10);
        assertEquals(classFile.getConstantPool().getConstantPoolCount(), 10);
        assertEquals(classFile.getMajorVersion(), 100);
        assertEquals(classFile.getMinorVersion(), 102);
        assertEquals(classFile.getMagic(), 1000);
        assertEquals(classFile.getFieldCount(), 20);
        assertEquals(classFile.getFields().length, 0);
        assertEquals(classFile.getInterfaceCount(), 1);
        assertEquals(classFile.getInterfaces()[0], Test.class.getName());
        assertEquals(classFile.getSuperClass(), TestCase.class.getName());
        assertEquals(classFile.getMethodCount(), 1);
        assertEquals(classFile.getMethods().length, 1);
    }
}

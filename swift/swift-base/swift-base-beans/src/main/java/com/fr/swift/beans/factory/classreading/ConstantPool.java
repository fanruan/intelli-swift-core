package com.fr.swift.beans.factory.classreading;

import com.fr.swift.beans.factory.classreading.basic.reader.ShortReader;

import java.io.InputStream;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ConstantPool {
    private int constantPoolCount;
    private ConstantInfo[] cpInfo;

    public ConstantPool(int count) {
        constantPoolCount = count;
        cpInfo = new ConstantInfo[constantPoolCount];
    }

    public void read(InputStream inputStream) {
        for (int i = 1; i < constantPoolCount; i++) {
            short tag = ShortReader.read(inputStream);
            ConstantInfo constantInfo = ConstantInfo.getConstantInfo(tag);
            constantInfo.read(inputStream);
            cpInfo[i] = constantInfo;
            if (tag == ConstantInfo.CONSTANT_Double || tag == ConstantInfo.CONSTANT_Long) {
                i++;
            }
        }
    }

    public int getConstantPoolCount() {
        return constantPoolCount;
    }

    public ConstantInfo[] getCpInfo() {
        return cpInfo;
    }
}

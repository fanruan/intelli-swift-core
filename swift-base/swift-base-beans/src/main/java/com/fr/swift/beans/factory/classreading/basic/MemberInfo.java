package com.fr.swift.beans.factory.classreading.basic;


import com.fr.swift.beans.factory.classreading.ConstantPool;
import com.fr.swift.beans.factory.classreading.basic.attribute.AttributeInfo;
import com.fr.swift.beans.factory.classreading.basic.reader.IntReader;

import java.io.InputStream;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description todo 预留，暂时没用到
 * @since Advanced FineBI 5.0
 */
public class MemberInfo extends BasicInfo {
    private int accessFlags;
    private int nameIndex;
    private int descriptorIndex;
    private int attributesCount;
    private AttributeInfo[] attributes;

    public MemberInfo(ConstantPool constantPool) {
        super(constantPool);
    }

    @Override
    public void read(InputStream inputStream) {
        accessFlags = IntReader.read(inputStream);
        nameIndex = IntReader.read(inputStream);
        descriptorIndex = IntReader.read(inputStream);
        attributesCount = IntReader.read(inputStream);
        attributes = new AttributeInfo[attributesCount];
        for (int i = 0; i < attributesCount; i++) {
            AttributeInfo attributeInfo = AttributeInfo.getAttribute(constantPool, inputStream);
            attributeInfo.read(inputStream);
            attributes[i] = attributeInfo;
        }
    }

    public int getAccessFlags() {
        return accessFlags;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    public int getAttributesCount() {
        return attributesCount;
    }

    public AttributeInfo[] getAttributes() {
        return attributes;
    }
}

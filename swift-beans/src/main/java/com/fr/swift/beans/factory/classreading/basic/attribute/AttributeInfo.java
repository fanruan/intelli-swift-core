package com.fr.swift.beans.factory.classreading.basic.attribute;

import com.fr.swift.beans.factory.classreading.ConstantPool;
import com.fr.swift.beans.factory.classreading.basic.BasicInfo;
import com.fr.swift.beans.factory.classreading.basic.reader.IntReader;
import com.fr.swift.beans.factory.classreading.basic.reader.LongReader;
import com.fr.swift.beans.factory.classreading.basic.reader.ShortReader;
import com.fr.swift.beans.factory.classreading.constant.ConstantUtf8;

import java.io.InputStream;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description todo 预留，暂时没用到
 * @since Advanced FineBI 5.0
 */
public class AttributeInfo extends BasicInfo {
    private int nameIndex;
    protected long length;
    private short[] info;
    private static final String CODE = "Code";

    public AttributeInfo(ConstantPool constantPool, int nameIndex) {
        super(constantPool);
        this.nameIndex = nameIndex;
    }

    @Override
    public void read(InputStream inputStream) {
        length = LongReader.read(inputStream);
        info = new short[(int) length];
        for (int i = 0; i < length; i++) {
            info[i] = ShortReader.read(inputStream);
        }
    }

    public static AttributeInfo getAttribute(ConstantPool cp, InputStream inputStream) {
        int nameIndex = IntReader.read(inputStream);
        String name = ((ConstantUtf8) cp.getCpInfo()[nameIndex]).getValue();
        if (CODE.equals(name)) {
            return new CodeAttribute(cp, nameIndex);
        }
        return new AttributeInfo(cp, nameIndex);
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public long getLength() {
        return length;
    }

    public short[] getInfo() {
        return info;
    }
}

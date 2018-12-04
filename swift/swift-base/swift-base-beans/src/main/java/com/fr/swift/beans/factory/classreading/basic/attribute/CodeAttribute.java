package com.fr.swift.beans.factory.classreading.basic.attribute;

import com.fr.swift.beans.factory.classreading.ConstantPool;
import com.fr.swift.beans.factory.classreading.basic.reader.IntReader;
import com.fr.swift.beans.factory.classreading.basic.reader.LongReader;
import com.fr.swift.beans.factory.classreading.basic.reader.ShortReader;

import java.io.InputStream;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description todo 预留，暂时没用到
 * @since Advanced FineBI 5.0
 */
public class CodeAttribute extends AttributeInfo {

    private int maxStack;
    private int maxLocals;
    private long codeLength;
    private short[] code;
    private int excepetionTableLength;
    private ExceptionTable[] exceptionTable;
    private int attributes_count;
    private AttributeInfo[] attributes;


    public CodeAttribute(ConstantPool cp, int nameIndex) {
        super(cp, nameIndex);
    }

    @Override
    public void read(InputStream inputStream) {
        length = LongReader.read(inputStream);

        maxStack = IntReader.read(inputStream);
        maxLocals = IntReader.read(inputStream);
        codeLength = LongReader.read(inputStream);
        code = new short[(int) codeLength];
        for (int i = 0; i < codeLength; i++) {
            code[i] = ShortReader.read(inputStream);
        }
        excepetionTableLength = IntReader.read(inputStream);
        exceptionTable = new ExceptionTable[excepetionTableLength];
        for (int i = 0; i < excepetionTableLength; i++) {
            ExceptionTable exceTable = new ExceptionTable();
            exceTable.read(inputStream);
            exceptionTable[i] = exceTable;
        }
        attributes_count = IntReader.read(inputStream);
        attributes = new AttributeInfo[attributes_count];
        for (int i = 0; i < attributes_count; i++) {
            AttributeInfo attr = AttributeInfo.getAttribute(constantPool, inputStream);
            attr.read(inputStream);
            attributes[i] = attr;
        }
    }

    public int getMaxStack() {
        return maxStack;
    }

    public int getMaxLocals() {
        return maxLocals;
    }

    public long getCodeLength() {
        return codeLength;
    }

    public short[] getCode() {
        return code;
    }

    public int getExcepetionTableLength() {
        return excepetionTableLength;
    }

    public ExceptionTable[] getExceptionTable() {
        return exceptionTable;
    }

    public int getAttributes_count() {
        return attributes_count;
    }

    public AttributeInfo[] getAttributes() {
        return attributes;
    }
}

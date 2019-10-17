package com.fr.swift.beans.factory.classreading.basic;

import com.fr.swift.beans.factory.classreading.ConstantPool;

import java.io.InputStream;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class BasicInfo {
    protected ConstantPool constantPool;

    public BasicInfo(ConstantPool constantPool) {
        this.constantPool = constantPool;
    }

    public abstract void read(InputStream inputStream);
}

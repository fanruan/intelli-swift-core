package com.fr.swift.cloud.beans.factory.classreading.constant;

import com.fr.swift.cloud.beans.factory.classreading.ConstantInfo;
import com.fr.swift.cloud.beans.factory.classreading.basic.reader.IntReader;

import java.io.InputStream;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ConstantString extends ConstantInfo<Integer> {
    private int nameIndex;

    @Override
    public void read(InputStream inputStream) {
        nameIndex = IntReader.read(inputStream);
    }

    public int getNameIndex() {
        return nameIndex;
    }
}

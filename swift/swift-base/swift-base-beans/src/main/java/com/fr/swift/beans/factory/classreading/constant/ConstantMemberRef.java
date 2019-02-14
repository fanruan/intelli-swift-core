package com.fr.swift.beans.factory.classreading.constant;

import com.fr.swift.beans.factory.classreading.ConstantInfo;
import com.fr.swift.beans.factory.classreading.basic.reader.IntReader;

import java.io.InputStream;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ConstantMemberRef extends ConstantInfo<Integer> {
    private int classIndex;
    private int nameAndTypeIndex;

    @Override
    public void read(InputStream inputStream) {
        classIndex = IntReader.read(inputStream);
        nameAndTypeIndex = IntReader.read(inputStream);
    }

    public int getClassIndex() {
        return classIndex;
    }

    public int getNameAndTypeIndex() {
        return nameAndTypeIndex;
    }
}

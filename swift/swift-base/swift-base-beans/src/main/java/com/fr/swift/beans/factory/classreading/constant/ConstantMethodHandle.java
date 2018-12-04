package com.fr.swift.beans.factory.classreading.constant;

import com.fr.swift.beans.factory.classreading.ConstantInfo;
import com.fr.swift.beans.factory.classreading.basic.reader.IntReader;
import com.fr.swift.beans.factory.classreading.basic.reader.ShortReader;

import java.io.InputStream;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ConstantMethodHandle extends ConstantInfo<Short> {
    private short referenceKind;
    private int referenceIndex;

    @Override
    public void read(InputStream inputStream) {
        referenceKind = ShortReader.read(inputStream);
        referenceIndex = IntReader.read(inputStream);
    }

    public short getReferenceKind() {
        return referenceKind;
    }

    public int getReferenceIndex() {
        return referenceIndex;
    }
}

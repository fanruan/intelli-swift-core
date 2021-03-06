package com.fr.swift.cloud.beans.factory.classreading.constant;

import com.fr.swift.cloud.beans.factory.classreading.ConstantInfo;
import com.fr.swift.cloud.beans.factory.classreading.basic.reader.LongReader;

import java.io.InputStream;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ConstantFloat extends ConstantInfo<Long> {

    private long value;

    @Override
    public void read(InputStream inputStream) {
        value = LongReader.read(inputStream);
    }

    public long getValue() {
        return value;
    }
}

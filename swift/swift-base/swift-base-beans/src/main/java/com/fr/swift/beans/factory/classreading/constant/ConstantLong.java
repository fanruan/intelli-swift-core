package com.fr.swift.beans.factory.classreading.constant;

import com.fr.swift.beans.factory.classreading.ConstantInfo;
import com.fr.swift.beans.factory.classreading.basic.reader.LongReader;

import java.io.InputStream;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ConstantLong extends ConstantInfo<Long> {
    private long highValue;
    private long lowValue;

    @Override
    public void read(InputStream inputStream) {
        highValue = LongReader.read(inputStream);
        lowValue = LongReader.read(inputStream);
    }

    public long getHighValue() {
        return highValue;
    }

    public long getLowValue() {
        return lowValue;
    }
}

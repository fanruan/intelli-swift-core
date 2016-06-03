package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.ICubeDoubleReaderWrapper;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDoubleReaderWrapper extends BICubePrimitiveReaderWrapper<Double> implements ICubeDoubleReaderWrapper {
    public BICubeDoubleReaderWrapper(ICubePrimitiveReader<Double> reader) {
        super(reader);

    }
}

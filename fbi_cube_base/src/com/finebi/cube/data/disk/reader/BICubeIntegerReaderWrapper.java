package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.ICubeIntegerReaderWrapper;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeIntegerReaderWrapper extends BICubePrimitiveReaderWrapper<Integer> implements ICubeIntegerReaderWrapper {
    public BICubeIntegerReaderWrapper(ICubePrimitiveReader<Integer> reader) {
        super(reader);
    }
}

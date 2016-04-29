package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.ICubeByteReaderWrapper;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeByteReaderWrapper extends BICubePrimitiveReaderWrapper<Byte> implements ICubeByteReaderWrapper {
    public BICubeByteReaderWrapper(ICubePrimitiveReader<Byte> reader) {
        super(reader);
    }
}

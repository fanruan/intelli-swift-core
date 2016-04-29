package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.ICubeLongReaderWrapper;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeLongReaderWrapper extends BICubePrimitiveReaderWrapper<Long> implements ICubeLongReaderWrapper {
    public BICubeLongReaderWrapper(ICubePrimitiveReader<Long> reader) {
        super(reader);
    }
}

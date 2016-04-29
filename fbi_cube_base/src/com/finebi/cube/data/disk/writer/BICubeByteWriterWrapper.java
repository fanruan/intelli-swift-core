package com.finebi.cube.data.disk.writer;

import com.finebi.cube.data.output.ICubeByteWriterWrapper;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeByteWriterWrapper extends BICubePrimitiveWriterWrapper<Byte> implements ICubeByteWriterWrapper {
    public BICubeByteWriterWrapper(ICubePrimitiveWriter<Byte> writer) {
        super(writer);
    }
}

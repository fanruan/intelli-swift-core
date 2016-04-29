package com.finebi.cube.data.disk.writer;

import com.finebi.cube.data.output.ICubeIntegerWriterWrapper;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeIntegerWriterWrapper extends BICubePrimitiveWriterWrapper<Integer> implements ICubeIntegerWriterWrapper {
    public BICubeIntegerWriterWrapper(ICubePrimitiveWriter<Integer> writer) {
        super(writer);
    }
}

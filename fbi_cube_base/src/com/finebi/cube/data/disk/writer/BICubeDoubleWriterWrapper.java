package com.finebi.cube.data.disk.writer;

import com.finebi.cube.data.output.ICubeDoubleWriterWrapper;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDoubleWriterWrapper extends BICubePrimitiveWriterWrapper<Double> implements ICubeDoubleWriterWrapper {
    public BICubeDoubleWriterWrapper(ICubePrimitiveWriter<Double> writer) {
        super(writer);
    }
}

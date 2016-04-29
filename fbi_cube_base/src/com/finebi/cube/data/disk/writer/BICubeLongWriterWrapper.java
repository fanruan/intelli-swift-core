package com.finebi.cube.data.disk.writer;

import com.finebi.cube.data.output.ICubeLongWriterWrapper;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeLongWriterWrapper extends BICubePrimitiveWriterWrapper<Long> implements ICubeLongWriterWrapper {
    public BICubeLongWriterWrapper(ICubePrimitiveWriter<Long> writer) {
        super(writer);
    }
}

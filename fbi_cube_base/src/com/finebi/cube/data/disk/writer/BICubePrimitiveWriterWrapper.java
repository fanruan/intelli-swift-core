package com.finebi.cube.data.disk.writer;

import com.finebi.cube.BICubeLongTypePosition;
import com.finebi.cube.data.output.ICubeWriter;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;

/**
 * This class created on 2016/3/30.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubePrimitiveWriterWrapper<T> implements ICubeWriter<T> {
    private ICubePrimitiveWriter<T> writer;

    public BICubePrimitiveWriterWrapper(ICubePrimitiveWriter<T> writer) {
        this.writer = writer;
    }

    @Override
    public void recordSpecificValue(int specificPosition, T value) {
        writer.recordSpecificPositionValue(specificPosition, value);
    }

    @Override
    public void saveStatus() {

    }

    @Override
    public void setPosition(BICubeLongTypePosition position) {

    }

    @Override
    public void releaseResource() {
        writer.releaseResource();
    }

    @Override
    public void flush() {
        writer.flush();
    }
}

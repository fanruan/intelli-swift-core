package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.ICubeReader;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;

/**
 * This class created on 2016/3/30.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubePrimitiveReaderWrapper implements ICubeReader {
    protected ICubePrimitiveReader reader;

    public BICubePrimitiveReaderWrapper(ICubePrimitiveReader reader) {
        this.reader = reader;
    }


    @Override
    public long getLastPosition(long rowCount) {
        return 0;
    }

    @Override
    public void clear() {
        reader.releaseHandler();
    }

    @Override
    public boolean canRead() {
        return reader.canReader();
    }

    @Override
    public void forceRelease() {
        reader.forceRelease();
    }

    @Override
    public boolean isForceReleased() {
        return reader.isForceReleased();
    }
}

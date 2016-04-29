package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.ICubeReader;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.finebi.cube.exception.BIResourceInvalidException;

/**
 * This class created on 2016/3/30.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubePrimitiveReaderWrapper<T> implements ICubeReader<T> {
    private ICubePrimitiveReader<T> reader;

    public BICubePrimitiveReaderWrapper(ICubePrimitiveReader<T> reader) {
        this.reader = reader;
    }

    @Override
    public T getSpecificValue(int rowNumber) throws BIResourceInvalidException {
        return reader.getSpecificValue(rowNumber);
    }

    @Override
    public long getLastPosition(long rowCount) {
        return 0;
    }

    @Override
    public void clear() {
        reader.clear();
    }

    @Override
    public boolean canRead() {
        return reader.canReader();
    }
}

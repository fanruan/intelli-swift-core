package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.ICubeIntegerReaderWrapper;
import com.finebi.cube.data.input.primitive.ICubeIntegerReader;
import com.finebi.cube.exception.BIResourceInvalidException;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeIntegerReaderWrapper extends BICubePrimitiveReaderWrapper implements ICubeIntegerReaderWrapper {
    public BICubeIntegerReaderWrapper(ICubeIntegerReader reader) {
        super(reader);
    }

    public int getSpecificValue(int rowNumber) throws BIResourceInvalidException {
        return ((ICubeIntegerReader)reader).getSpecificValue(rowNumber);
    }
}

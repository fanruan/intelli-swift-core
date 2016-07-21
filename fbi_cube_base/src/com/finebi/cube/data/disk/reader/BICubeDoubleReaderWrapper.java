package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.ICubeDoubleReaderWrapper;
import com.finebi.cube.data.input.primitive.ICubeDoubleReader;
import com.finebi.cube.exception.BIResourceInvalidException;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDoubleReaderWrapper extends BICubePrimitiveReaderWrapper implements ICubeDoubleReaderWrapper {
    public BICubeDoubleReaderWrapper(ICubeDoubleReader reader) {
        super(reader);
    }

    public double getSpecificValue(int rowNumber) throws BIResourceInvalidException {
        return ((ICubeDoubleReader)reader).getSpecificValue(rowNumber);
    }
}

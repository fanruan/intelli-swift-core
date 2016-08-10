package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.ICubeByteReaderWrapper;
import com.finebi.cube.data.input.primitive.ICubeByteReader;
import com.finebi.cube.exception.BIResourceInvalidException;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeByteReaderWrapper extends BICubePrimitiveReaderWrapper implements ICubeByteReaderWrapper {
    public BICubeByteReaderWrapper(ICubeByteReader reader) {
        super(reader);
    }
    public byte getSpecificValue(int rowNumber) throws BIResourceInvalidException {
        return ((ICubeByteReader)reader).getSpecificValue(rowNumber);
    }
}

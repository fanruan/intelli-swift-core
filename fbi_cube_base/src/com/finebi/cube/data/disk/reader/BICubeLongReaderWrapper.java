package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.ICubeLongReaderWrapper;
import com.finebi.cube.data.input.primitive.ICubeLongReader;
import com.finebi.cube.exception.BIResourceInvalidException;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeLongReaderWrapper extends BICubePrimitiveReaderWrapper implements ICubeLongReaderWrapper {
    public BICubeLongReaderWrapper(ICubeLongReader reader) {
        super(reader);
    }

    public long getSpecificValue(int rowNumber) throws BIResourceInvalidException{
        return ((ICubeLongReader)reader).getSpecificValue(rowNumber);
    }
}

package com.finebi.cube.data.input.primitive;

import com.finebi.cube.exception.BIResourceInvalidException;

/**
 * This class created on 2016/3/4.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeIntegerReader extends ICubePrimitiveReader {
    int getSpecificValue(long filePosition) throws BIResourceInvalidException;

}

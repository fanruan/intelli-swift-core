package com.finebi.cube.data.input.primitive;

import com.finebi.cube.exception.BIResourceInvalidException;

/**
 * This class created on 2016/3/4.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeByteReader extends ICubePrimitiveReader {
    byte getSpecificValue(long filePosition) throws BIResourceInvalidException;

}

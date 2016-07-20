package com.finebi.cube.data.input.primitive;

import com.finebi.cube.exception.BIResourceInvalidException;

/**
 * This class created on 2016/3/4.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeDoubleReader extends ICubePrimitiveReader {
    double getSpecificValue(long filePosition) throws BIResourceInvalidException;

}

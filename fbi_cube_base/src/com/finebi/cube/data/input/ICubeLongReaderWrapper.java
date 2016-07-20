package com.finebi.cube.data.input;

import com.finebi.cube.exception.BIResourceInvalidException;

/**
 * This class created on 2016/3/4.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeLongReaderWrapper extends ICubeReader {
    long getSpecificValue(int rowNumber) throws BIResourceInvalidException;
}

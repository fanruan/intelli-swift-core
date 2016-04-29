package com.finebi.cube.data.input;

import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.location.ICubeResourceLocation;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeReaderBuilder<T> {
    String QUERY_TAG = "Reader";

    T buildReader(ICubeResourceLocation resourceLocation) throws BIBuildReaderException;

}

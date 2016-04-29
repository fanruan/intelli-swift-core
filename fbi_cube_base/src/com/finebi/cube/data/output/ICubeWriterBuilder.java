package com.finebi.cube.data.output;

import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.location.ICubeResourceLocation;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeWriterBuilder<T> {
    String QUERY_TAG = "Writer";

    T buildWriter(ICubeResourceLocation resourceLocation)throws BIBuildWriterException;
}

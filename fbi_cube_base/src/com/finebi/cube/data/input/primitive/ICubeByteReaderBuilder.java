package com.finebi.cube.data.input.primitive;

import com.finebi.cube.data.input.ICubeReaderBuilder;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeByteReaderBuilder extends ICubeReaderBuilder<ICubeByteReader> {
    String FRAGMENT_TAG = "BYTE";
}

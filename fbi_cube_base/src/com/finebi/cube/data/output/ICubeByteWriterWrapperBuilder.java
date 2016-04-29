package com.finebi.cube.data.output;

import com.finebi.cube.data.input.ICubeByteReaderWrapperBuilder;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeByteWriterWrapperBuilder extends ICubeWriterBuilder<ICubeByteWriterWrapper> {
    String FRAGMENT_TAG = ICubeByteReaderWrapperBuilder.FRAGMENT_TAG;
}

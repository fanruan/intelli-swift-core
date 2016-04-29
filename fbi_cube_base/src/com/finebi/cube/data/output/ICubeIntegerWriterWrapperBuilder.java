package com.finebi.cube.data.output;

import com.finebi.cube.data.input.ICubeIntegerReaderWrapperBuilder;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeIntegerWriterWrapperBuilder extends ICubeWriterBuilder<ICubeIntegerWriterWrapper> {
    String FRAGMENT_TAG = ICubeIntegerReaderWrapperBuilder.FRAGMENT_TAG;
}

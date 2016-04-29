package com.finebi.cube.data.output;

import com.finebi.cube.data.input.ICubeDoubleReaderWrapperBuilder;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeDoubleWriterWrapperBuilder extends ICubeWriterBuilder<ICubeDoubleWriterWrapper> {
    String FRAGMENT_TAG = ICubeDoubleReaderWrapperBuilder.FRAGMENT_TAG;
}

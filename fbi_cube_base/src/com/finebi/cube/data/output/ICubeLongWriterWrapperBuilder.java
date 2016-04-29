package com.finebi.cube.data.output;

import com.finebi.cube.data.input.ICubeLongReaderWrapperBuilder;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeLongWriterWrapperBuilder extends ICubeWriterBuilder<ICubeLongWriterWrapper> {
    String FRAGMENT_TAG = ICubeLongReaderWrapperBuilder.FRAGMENT_TAG;
}

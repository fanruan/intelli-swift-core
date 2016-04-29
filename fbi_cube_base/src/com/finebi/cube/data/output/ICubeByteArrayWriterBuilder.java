package com.finebi.cube.data.output;

import com.finebi.cube.data.input.ICubeByteArrayReaderBuilder;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeByteArrayWriterBuilder extends ICubeWriterBuilder<ICubeByteArrayWriter> {
    String FRAGMENT_TAG = ICubeByteArrayReaderBuilder.FRAGMENT_TAG;
}

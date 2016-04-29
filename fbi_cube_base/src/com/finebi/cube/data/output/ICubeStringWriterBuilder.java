package com.finebi.cube.data.output;

import com.finebi.cube.data.input.ICubeStringReaderBuilder;

/**
 * This class created on 2016/3/11.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeStringWriterBuilder extends ICubeWriterBuilder<ICubeStringWriter> {
    String FRAGMENT_TAG = ICubeStringReaderBuilder.FRAGMENT_TAG;
}

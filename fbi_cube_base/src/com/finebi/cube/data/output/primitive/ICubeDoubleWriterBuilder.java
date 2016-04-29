package com.finebi.cube.data.output.primitive;

import com.finebi.cube.data.input.primitive.ICubeDoubleReaderBuilder;
import com.finebi.cube.data.output.ICubeWriterBuilder;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeDoubleWriterBuilder extends ICubeWriterBuilder<ICubeDoubleWriter> {
    String FRAGMENT_TAG = ICubeDoubleReaderBuilder.FRAGMENT_TAG;

}

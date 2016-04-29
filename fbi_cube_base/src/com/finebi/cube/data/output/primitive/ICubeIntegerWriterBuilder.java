package com.finebi.cube.data.output.primitive;

import com.finebi.cube.data.input.primitive.ICubeIntegerReaderBuilder;
import com.finebi.cube.data.output.ICubeWriterBuilder;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeIntegerWriterBuilder extends ICubeWriterBuilder<ICubeIntegerWriter> {
    String FRAGMENT_TAG = ICubeIntegerReaderBuilder.FRAGMENT_TAG;

}

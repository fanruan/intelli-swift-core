package com.finebi.cube.data.output.primitive;

import com.finebi.cube.data.input.primitive.ICubeByteReaderBuilder;
import com.finebi.cube.data.output.ICubeWriterBuilder;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeByteWriterBuilder extends ICubeWriterBuilder<ICubeByteWriter> {
    String FRAGMENT_TAG = ICubeByteReaderBuilder.FRAGMENT_TAG;

}

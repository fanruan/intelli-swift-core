package com.finebi.cube.data.output.primitive;

import com.finebi.cube.data.input.primitive.ICubeLongReaderBuilder;
import com.finebi.cube.data.output.ICubeWriterBuilder;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeLongWriterBuilder extends ICubeWriterBuilder<ICubeLongWriter> {
    String FRAGMENT_TAG = ICubeLongReaderBuilder.FRAGMENT_TAG;
}

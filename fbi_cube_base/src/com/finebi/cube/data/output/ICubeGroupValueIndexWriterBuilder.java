package com.finebi.cube.data.output;

import com.finebi.cube.data.input.ICubeGroupValueIndexReaderBuilder;

/**
 * Created by naleite on 16/3/15.
 */
public interface ICubeGroupValueIndexWriterBuilder extends ICubeWriterBuilder<ICubeGroupValueIndexWriter>{
    String FRAGMENT_TAG = ICubeGroupValueIndexReaderBuilder.FRAGMENT_TAG;
}

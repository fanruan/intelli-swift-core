package com.finebi.cube.data.disk.writer;

import com.finebi.cube.data.input.ICubeByteArrayReaderBuilder;
import com.finebi.cube.data.output.*;

/**
 * This class created on 2016/3/16.
 *
 * @author Connery
 * @since 4.0
 */
public class BINIOWriterIncreaseManager extends BIBasicNIOWriterManager<ICubeWriter> {
    private static BINIOWriterIncreaseManager instance;

    public static BINIOWriterIncreaseManager getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (BINIOWriterIncreaseManager.class) {
                if (instance == null) {
                    instance = new BINIOWriterIncreaseManager();
                }
                return instance;
            }
        }
    }

    private BINIOWriterIncreaseManager() {
        super();
        tag2Builder.put(ICubeStringWriterBuilder.FRAGMENT_TAG, new BIStringNIOIncreaseWriterBuilder());
//        tag2Builder.put(ICubeStringWriterBuilder.FRAGMENT_TAG, new BIStringNIOWriterBuilder());
        tag2Builder.put(ICubeByteArrayReaderBuilder.FRAGMENT_TAG, new BIByteArrayNIOWriterIncreaseBuilder());
        tag2Builder.put(ICubeGroupValueIndexWriterBuilder.FRAGMENT_TAG, new BIGroupValueIndexWriterBuilder());
        tag2Builder.put(ICubeByteWriterWrapperBuilder.FRAGMENT_TAG, new BICubeByteWriterWrapperBuilder());
        tag2Builder.put(ICubeDoubleWriterWrapperBuilder.FRAGMENT_TAG, new BICubeDoubleWriterWrapperBuilder());
        tag2Builder.put(ICubeIntegerWriterWrapperBuilder.FRAGMENT_TAG, new BICubeIntegerWriterWrapperBuilder());
        tag2Builder.put(ICubeLongWriterWrapperBuilder.FRAGMENT_TAG, new BICubeLongWriterWrapperBuilder());

    }
}
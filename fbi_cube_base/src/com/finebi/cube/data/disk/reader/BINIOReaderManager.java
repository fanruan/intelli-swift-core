package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.input.*;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BINIOReaderManager extends BIBasicNIOReaderManager<ICubeReader> {
    private static BINIOReaderManager instance;

    public static BINIOReaderManager getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (BINIOReaderManager.class) {
                if (instance == null) {
                    instance = new BINIOReaderManager();
                }
                return instance;
            }
        }
    }

    private BINIOReaderManager() {
        super();
        tag2Builder.put(ICubeByteArrayReaderBuilder.FRAGMENT_TAG, new BIByteArrayNIOReaderBuilder());
        tag2Builder.put(ICubeStringReaderBuilder.FRAGMENT_TAG, new BIStringNIOReaderBuilder());
        tag2Builder.put(ICubeGroupValueIndexReaderBuilder.FRAGMENT_TAG, new BIGroupValueIndexNIOReaderBuilder());
        tag2Builder.put(ICubeByteReaderWrapperBuilder.FRAGMENT_TAG, new BICubeByteReaderWrapperBuilder());
        tag2Builder.put(ICubeDoubleReaderWrapperBuilder.FRAGMENT_TAG, new BICubeDoubleReaderWrapperBuilder());
        tag2Builder.put(ICubeIntegerReaderWrapperBuilder.FRAGMENT_TAG, new BICubeIntegerReaderWrapperBuilder());
        tag2Builder.put(ICubeLongReaderWrapperBuilder.FRAGMENT_TAG, new BICubeLongReaderWrapperBuilder());

    }


}

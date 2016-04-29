package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.*;
import com.finebi.cube.data.disk.reader.BIBasicNIOReaderManager;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIPrimitiveNIOReaderManager extends BIBasicNIOReaderManager<ICubePrimitiveReader> {
    private static BIPrimitiveNIOReaderManager instance;

    public static BIPrimitiveNIOReaderManager getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (BIPrimitiveNIOReaderManager.class) {
                if (instance == null) {
                    instance = new BIPrimitiveNIOReaderManager();
                }
                return instance;
            }
        }
    }

    private BIPrimitiveNIOReaderManager() {
        super();
        tag2Builder.put(ICubeByteReaderBuilder.FRAGMENT_TAG, new BIByteNIOReaderBuilder());
        tag2Builder.put(ICubeLongReaderBuilder.FRAGMENT_TAG, new BILongNIOReaderBuilder());
        tag2Builder.put(ICubeIntegerReaderBuilder.FRAGMENT_TAG, new BIIntegerNIOReaderBuilder());
        tag2Builder.put(ICubeDoubleReaderBuilder.FRAGMENT_TAG, new BIDoubleNIOReaderBuilder());

    }


}

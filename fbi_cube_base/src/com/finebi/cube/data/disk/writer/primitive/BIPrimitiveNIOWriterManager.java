package com.finebi.cube.data.disk.writer.primitive;

import com.finebi.cube.data.output.primitive.*;
import com.finebi.cube.data.disk.writer.BIBasicNIOWriterManager;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIPrimitiveNIOWriterManager extends BIBasicNIOWriterManager<ICubePrimitiveWriter> {
    private static BIPrimitiveNIOWriterManager instance;

    public static BIPrimitiveNIOWriterManager getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (BIPrimitiveNIOWriterManager.class) {
                if (instance == null) {
                    instance = new BIPrimitiveNIOWriterManager();
                }
                return instance;
            }
        }
    }

    protected BIPrimitiveNIOWriterManager() {
        super();
        tag2Builder.put(ICubeByteWriterBuilder.FRAGMENT_TAG, new BIByteNIOWriterBuilder());
        tag2Builder.put(ICubeLongWriterBuilder.FRAGMENT_TAG, new BILongNIOWriterBuilder());
        tag2Builder.put(ICubeIntegerWriterBuilder.FRAGMENT_TAG, new BIIntegerNIOWriterBuilder());
        tag2Builder.put(ICubeDoubleWriterBuilder.FRAGMENT_TAG, new BIDoubleNIOWriterBuilder());
    }


}

package com.finebi.cube.data.disk.writer.primitive;

import com.finebi.cube.data.output.primitive.ICubeDoubleWriter;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.data.output.primitive.ICubeDoubleWriterBuilder;

import java.io.File;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIDoubleNIOWriterBuilder extends BIPrimitiveNIOWriterBuilder<ICubeDoubleWriter> implements ICubeDoubleWriterBuilder {
    @Override
    protected String getFragmentTag() {
        return BIDoubleNIOWriterBuilder.FRAGMENT_TAG;
    }

    @Override
    protected ICubeDoubleWriter createNIOWriter(File target, ICubeResourceLocation location) {
        return new BIDoubleNIOWriter(target);
    }
}

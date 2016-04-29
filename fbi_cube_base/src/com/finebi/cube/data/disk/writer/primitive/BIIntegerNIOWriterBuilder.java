package com.finebi.cube.data.disk.writer.primitive;

import com.finebi.cube.data.output.primitive.ICubeIntegerWriterBuilder;
import com.finebi.cube.data.output.primitive.ICubeIntegerWriter;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIIntegerNIOWriterBuilder extends BIPrimitiveNIOWriterBuilder<ICubeIntegerWriter> implements ICubeIntegerWriterBuilder {
    @Override
    protected String getFragmentTag() {
        return BIIntegerNIOWriterBuilder.FRAGMENT_TAG;
    }

    @Override
    protected ICubeIntegerWriter createNIOWriter(File target, ICubeResourceLocation location) {
        return new BIIntegerNIOWriter(target);
    }
}

package com.finebi.cube.data.disk.writer.primitive;

import com.finebi.cube.data.output.primitive.ICubeByteWriter;
import com.finebi.cube.data.output.primitive.ICubeByteWriterBuilder;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIByteNIOWriterBuilder extends BIPrimitiveNIOWriterBuilder<ICubeByteWriter> implements ICubeByteWriterBuilder {
    @Override
    protected String getFragmentTag() {
        return BIByteNIOWriterBuilder.FRAGMENT_TAG;
    }

    @Override
    protected ICubeByteWriter createNIOWriter(File target, ICubeResourceLocation location) {
        return new BIByteNIOWriter(target);
    }
}

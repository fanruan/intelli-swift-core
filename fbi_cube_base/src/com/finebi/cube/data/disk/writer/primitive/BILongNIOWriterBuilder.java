package com.finebi.cube.data.disk.writer.primitive;

import com.finebi.cube.data.output.primitive.ICubeLongWriter;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.data.output.primitive.ICubeLongWriterBuilder;

import java.io.File;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BILongNIOWriterBuilder extends BIPrimitiveNIOWriterBuilder<ICubeLongWriter> implements ICubeLongWriterBuilder {
    @Override
    protected String getFragmentTag() {
        return BILongNIOWriterBuilder.FRAGMENT_TAG;
    }

    @Override
    protected ICubeLongWriter createNIOWriter(File target, ICubeResourceLocation location) {
        return new BILongNIOWriter(target);
    }
}

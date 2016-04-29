package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeByteReaderBuilder;
import com.finebi.cube.data.input.primitive.ICubeByteReader;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIByteNIOReaderBuilder extends BIPrimitiveNIOReaderBuilder<ICubeByteReader> implements ICubeByteReaderBuilder {
    @Override
    protected String getFragmentTag() {
        return ICubeByteReaderBuilder.FRAGMENT_TAG;
    }

    @Override
    protected ICubeByteReader createNIOReader(File target, ICubeResourceLocation targetLocation) {
        return new BIByteNIOReader(target);
    }
}

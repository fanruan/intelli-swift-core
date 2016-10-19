package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeDoubleReader;
import com.finebi.cube.data.input.primitive.ICubeDoubleReaderBuilder;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIDoubleNIOReaderBuilder extends BIPrimitiveNIOReaderBuilder<ICubeDoubleReader> implements ICubeDoubleReaderBuilder {
    @Override
    protected String getFragmentTag() {
        return ICubeDoubleReaderBuilder.FRAGMENT_TAG;
    }

    @Override
    protected ICubeDoubleReader createNIOReader(File target, ICubeResourceLocation targetLocation) {
        return new BIDoubleNIOReader(target);
    }
}

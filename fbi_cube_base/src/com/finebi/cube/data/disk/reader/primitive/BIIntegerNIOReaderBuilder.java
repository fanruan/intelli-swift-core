package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeIntegerReader;
import com.finebi.cube.data.input.primitive.ICubeIntegerReaderBuilder;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIIntegerNIOReaderBuilder extends BIPrimitiveNIOReaderBuilder<ICubeIntegerReader> implements ICubeIntegerReaderBuilder {
    @Override
    protected String getFragmentTag() {
        return ICubeIntegerReaderBuilder.FRAGMENT_TAG;
    }

    @Override
    protected ICubeIntegerReader createNIOReader(File target, ICubeResourceLocation targetLocation) {
        return new BIIntegerNIOReader(target);
    }
}

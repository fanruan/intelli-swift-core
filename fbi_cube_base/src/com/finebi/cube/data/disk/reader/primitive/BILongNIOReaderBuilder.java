package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.input.primitive.ICubeLongReader;
import com.finebi.cube.data.input.primitive.ICubeLongReaderBuilder;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BILongNIOReaderBuilder extends BIPrimitiveNIOReaderBuilder<ICubeLongReader> implements ICubeLongReaderBuilder {
    @Override
    protected String getFragmentTag() {
        return ICubeLongReaderBuilder.FRAGMENT_TAG;
    }

    @Override
    protected ICubeLongReader createNIOReader(File target, ICubeResourceLocation targetLocation) {
        return new BILongNIOReader(target);
    }
}

package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.data.input.ICubeLongReaderWrapper;
import com.finebi.cube.data.input.ICubeLongReaderWrapperBuilder;
import com.finebi.cube.data.input.primitive.ICubeLongReader;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeLongReaderWrapperBuilder extends BINIOReaderBuilder<ICubeLongReaderWrapper> implements ICubeLongReaderWrapperBuilder {
    @Override
    protected String getFragmentTag() {
        return this.FRAGMENT_TAG;
    }

    @Override
    protected ICubeLongReaderWrapper createNIOReader(File target, ICubeResourceLocation targetLocation) {
        ICubeResourceLocation contentLocation = targetLocation.copy();
        contentLocation.setReaderSourceLocation();
        contentLocation.setLongType();
        try {
            return new BICubeLongReaderWrapper((ICubeLongReader) BICubeDiskPrimitiveDiscovery.getInstance().getCubeReader(contentLocation));
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }
}

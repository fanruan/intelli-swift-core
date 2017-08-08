package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.data.input.ICubeIntegerReaderWrapper;
import com.finebi.cube.data.input.ICubeIntegerReaderWrapperBuilder;
import com.finebi.cube.data.input.primitive.ICubeIntegerReader;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeIntegerReaderWrapperBuilder extends BINIOReaderBuilder<ICubeIntegerReaderWrapper> implements ICubeIntegerReaderWrapperBuilder {
    @Override
    protected String getFragmentTag() {
        return this.FRAGMENT_TAG;
    }

    @Override
    protected ICubeIntegerReaderWrapper createNIOReader(File target, ICubeResourceLocation targetLocation) {
        try {
            ICubeResourceLocation contentLocation = targetLocation.copy().getRealLocation();
            contentLocation.setReaderSourceLocation();
            contentLocation.setIntegerType();
            return new BICubeIntegerReaderWrapper((ICubeIntegerReader) BICubeDiskPrimitiveDiscovery.getInstance().getCubeReader(contentLocation));
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }
}

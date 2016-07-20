package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.data.input.ICubeByteReaderWrapper;
import com.finebi.cube.data.input.ICubeByteReaderWrapperBuilder;
import com.finebi.cube.data.input.primitive.ICubeByteReader;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeByteReaderWrapperBuilder extends BINIOReaderBuilder<ICubeByteReaderWrapper> implements ICubeByteReaderWrapperBuilder {
    @Override
    protected String getFragmentTag() {
        return this.FRAGMENT_TAG;
    }

    @Override
    protected ICubeByteReaderWrapper createNIOReader(File target, ICubeResourceLocation targetLocation) {
        ICubeResourceLocation contentLocation = targetLocation.copy();
        contentLocation.setReaderSourceLocation();
        contentLocation.setByteType();
        try {
            return new BICubeByteReaderWrapper((ICubeByteReader) BICubeDiskPrimitiveDiscovery.getInstance().getCubeReader(contentLocation));
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }
}

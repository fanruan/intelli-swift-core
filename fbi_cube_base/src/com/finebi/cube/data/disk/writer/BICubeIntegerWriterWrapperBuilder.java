package com.finebi.cube.data.disk.writer;

import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.data.output.ICubeIntegerWriterWrapper;
import com.finebi.cube.data.output.ICubeIntegerWriterWrapperBuilder;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeIntegerWriterWrapperBuilder extends BINIOWriterBuilder<ICubeIntegerWriterWrapper> implements ICubeIntegerWriterWrapperBuilder {
    @Override
    protected String getFragmentTag() {
        return this.FRAGMENT_TAG;
    }

    @Override
    protected ICubeIntegerWriterWrapper createNIOWriter(File target, ICubeResourceLocation location) {
        ICubeResourceLocation contentLocation = location.copy();
        contentLocation.setWriterSourceLocation();
        contentLocation.setIntegerType();
        try {
            return new BICubeIntegerWriterWrapper(BICubeDiskPrimitiveDiscovery.getInstance().getCubeWriter(contentLocation));
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }
}

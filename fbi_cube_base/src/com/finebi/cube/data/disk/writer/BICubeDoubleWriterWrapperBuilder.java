package com.finebi.cube.data.disk.writer;

import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.data.output.ICubeDoubleWriterWrapper;
import com.finebi.cube.data.output.ICubeDoubleWriterWrapperBuilder;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDoubleWriterWrapperBuilder extends BINIOWriterBuilder<ICubeDoubleWriterWrapper> implements ICubeDoubleWriterWrapperBuilder {
    @Override
    protected String getFragmentTag() {
        return this.FRAGMENT_TAG;
    }

    @Override
    protected ICubeDoubleWriterWrapper createNIOWriter(File target, ICubeResourceLocation location) {
        ICubeResourceLocation contentLocation = location.copy();
        contentLocation.setWriterSourceLocation();
        contentLocation.setDoubleType();
        try {
            return new BICubeDoubleWriterWrapper(BICubeDiskPrimitiveDiscovery.getInstance().getCubeWriter(contentLocation));
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }
}

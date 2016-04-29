package com.finebi.cube.data.disk.writer;

import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.data.output.ICubeByteWriterWrapper;
import com.finebi.cube.data.output.ICubeByteWriterWrapperBuilder;
import com.finebi.cube.data.output.primitive.ICubeByteWriter;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeByteWriterWrapperBuilder extends BINIOWriterBuilder<ICubeByteWriterWrapper> implements ICubeByteWriterWrapperBuilder {
    @Override
    protected String getFragmentTag() {
        return this.FRAGMENT_TAG;
    }

    @Override
    protected ICubeByteWriterWrapper createNIOWriter(File target, ICubeResourceLocation location) {
        ICubeResourceLocation contentLocation = location.copy();
        contentLocation.setWriterSourceLocation();
        contentLocation.setByteType();
        try {
            return new BICubeByteWriterWrapper( BICubeDiskPrimitiveDiscovery.getInstance().getCubeWriter(contentLocation));
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }
}

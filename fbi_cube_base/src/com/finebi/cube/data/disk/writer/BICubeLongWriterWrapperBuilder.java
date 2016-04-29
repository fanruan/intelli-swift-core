package com.finebi.cube.data.disk.writer;

import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.data.output.ICubeLongWriterWrapper;
import com.finebi.cube.data.output.ICubeLongWriterWrapperBuilder;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeLongWriterWrapperBuilder extends BINIOWriterBuilder<ICubeLongWriterWrapper> implements ICubeLongWriterWrapperBuilder {
    @Override
    protected String getFragmentTag() {
        return this.FRAGMENT_TAG;
    }

    @Override
    protected ICubeLongWriterWrapper createNIOWriter(File target, ICubeResourceLocation location) {
        ICubeResourceLocation contentLocation = location.copy();
        contentLocation.setWriterSourceLocation();
        contentLocation.setLongType();
        try {
            return new BICubeLongWriterWrapper( BICubeDiskPrimitiveDiscovery.getInstance().getCubeWriter(contentLocation));
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }
}

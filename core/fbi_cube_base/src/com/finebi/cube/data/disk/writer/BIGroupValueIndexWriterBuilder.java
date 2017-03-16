package com.finebi.cube.data.disk.writer;

import com.finebi.cube.data.disk.BICubeDiskDiscovery;
import com.finebi.cube.data.output.ICubeByteArrayWriter;
import com.finebi.cube.data.output.ICubeGroupValueIndexWriter;
import com.finebi.cube.data.output.ICubeGroupValueIndexWriterBuilder;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * Created by naleite on 16/3/15.
 */
public class BIGroupValueIndexWriterBuilder extends BINIOWriterBuilder<ICubeGroupValueIndexWriter> implements ICubeGroupValueIndexWriterBuilder {
    @Override
    protected String getFragmentTag() {
        return ICubeGroupValueIndexWriterBuilder.FRAGMENT_TAG;
    }

    @Override
    protected ICubeGroupValueIndexWriter createNIOWriter(File target, ICubeResourceLocation location) {
        try {
            ICubeResourceLocation byteArrayLocation = location.copy();
            byteArrayLocation.setByteArrayType();
            ICubeByteArrayWriter byteArrayWriter = (BIByteArrayNIOWriter) BICubeDiskDiscovery.getInstance().getCubeWriter(byteArrayLocation);
            return new BIGroupValueIndexNIOWriter(byteArrayWriter);
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }

}

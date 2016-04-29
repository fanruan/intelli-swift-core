package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.disk.BICubeDiskDiscovery;
import com.finebi.cube.data.input.ICubeByteArrayReader;
import com.finebi.cube.data.input.ICubeGroupValueIndexReader;
import com.finebi.cube.data.input.ICubeGroupValueIndexReaderBuilder;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * Created by naleite on 16/3/15.
 */
public class BIGroupValueIndexNIOReaderBuilder extends BINIOReaderBuilder<ICubeGroupValueIndexReader> implements ICubeGroupValueIndexReaderBuilder {

    @Override
    protected String getFragmentTag() {
        return ICubeGroupValueIndexReaderBuilder.FRAGMENT_TAG;
    }

    @Override
    protected ICubeGroupValueIndexReader createNIOReader(File target, ICubeResourceLocation targetLocation) {

        try {
            ICubeResourceLocation byteArrayLocation = targetLocation.copy();
            byteArrayLocation.setByteArrayType();
            ICubeByteArrayReader byteArrayReader = (BIByteArrayNIOReader) BICubeDiskDiscovery.getInstance().getCubeReader(byteArrayLocation);
            return new BIGroupValueIndexNIOReader(byteArrayReader);
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }
}

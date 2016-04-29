package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.disk.BICubeDiskDiscovery;
import com.finebi.cube.data.input.ICubeByteArrayReader;
import com.finebi.cube.data.input.ICubeStringReader;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.data.input.ICubeStringReaderBuilder;

import java.io.File;

/**
 * This class created on 2016/3/11.
 *
 * @author Connery
 * @since 4.0
 */
public class BIStringNIOReaderBuilder extends BINIOReaderBuilder<ICubeStringReader> implements ICubeStringReaderBuilder {

    @Override
    protected String getFragmentTag() {
        return this.FRAGMENT_TAG;
    }

    @Override
    protected ICubeStringReader createNIOReader(File target, ICubeResourceLocation targetLocation) {
        try {
            ICubeResourceLocation byteArrayLocation = targetLocation.copy();
            byteArrayLocation.setByteArrayType();
            ICubeByteArrayReader byteArrayReader = (BIByteArrayNIOReader) BICubeDiskDiscovery.getInstance().getCubeReader(byteArrayLocation);
            return new BIStringNIOReader(byteArrayReader);
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }


}

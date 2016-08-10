package com.finebi.cube.data.disk.writer;

import com.finebi.cube.data.disk.BICubeIncreaseDisDiscovery;
import com.finebi.cube.data.output.ICubeByteArrayWriter;
import com.finebi.cube.data.output.ICubeStringWriter;
import com.finebi.cube.data.output.ICubeStringWriterBuilder;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/8/05.
 *
 * @author kary
 * @since 4.0
 */
public class BIStringNIOIncreaseWriterBuilder extends BINIOWriterBuilder<ICubeStringWriter> implements ICubeStringWriterBuilder {

    @Override
    protected String getFragmentTag() {
        return this.FRAGMENT_TAG;
    }

    @Override
    protected ICubeStringWriter createNIOWriter(File target, ICubeResourceLocation location) {
        try {
            ICubeResourceLocation byteArrayLocation = location.copy();
            byteArrayLocation.setByteArrayType();
            ICubeByteArrayWriter byteArrayWriter = (BIByteArrayNIOIncreaseWriter) BICubeIncreaseDisDiscovery.getInstance().getCubeWriter(byteArrayLocation);
            return new BIStringNIOWriter(byteArrayWriter);
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }
}

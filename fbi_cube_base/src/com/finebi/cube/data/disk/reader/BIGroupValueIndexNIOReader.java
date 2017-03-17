package com.finebi.cube.data.disk.reader;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.input.ICubeByteArrayReader;
import com.finebi.cube.data.input.ICubeGroupValueIndexReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by naleite on 16/3/15.
 */
public class BIGroupValueIndexNIOReader implements ICubeGroupValueIndexReader {

    protected ICubeByteArrayReader byteArray;

    public BIGroupValueIndexNIOReader(ICubeByteArrayReader byteList) {
        this.byteArray = byteList;
    }


    @Override
    public GroupValueIndex getSpecificValue(final int rowNumber) throws BIResourceInvalidException {
        try {
            return GVIFactory.createGroupValueIndexByDataInput(byteArray.getByteStream(rowNumber));
        } catch (BIResourceInvalidException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        throw new BIResourceInvalidException();
    }

    @Override
    public long getLastPosition(long rowCount) {
        return byteArray.getLastPosition(rowCount);
    }

    @Override
    public void clear() {
        if (byteArray != null) {
            byteArray.clear();
//            byteArray = null;
        }
    }

    @Override
    public boolean canRead() {
        return byteArray != null && byteArray.canRead();
    }

    @Override
    public void forceRelease() {
        byteArray.forceRelease();
    }

    @Override
    public boolean isForceReleased() {
        return byteArray.isForceReleased();
    }
}

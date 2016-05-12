package com.finebi.cube.data.disk.writer;

import com.finebi.cube.BICubeLongTypePosition;
import com.finebi.cube.data.output.ICubeByteArrayWriter;
import com.finebi.cube.data.output.ICubeGroupValueIndexWriter;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by naleite on 16/3/15.
 */
public class BIGroupValueIndexNIOWriter implements ICubeGroupValueIndexWriter {

    private ICubeByteArrayWriter bytesArray;

    public BIGroupValueIndexNIOWriter(ICubeByteArrayWriter byteWriteMappedList) {
        this.bytesArray = byteWriteMappedList;
    }

    @Override
    public void recordSpecificValue(int specificPosition, GroupValueIndex value) {

        byte[] bytes = null;
        if (value != null) {
            GroupValueIndex temp = value;
            bytes = temp.getBytes();
        }
        bytesArray.recordSpecificValue(specificPosition, bytes);
    }

    @Override
    public void saveStatus() {

    }

    @Override
    public void setPosition(BICubeLongTypePosition position) {

        bytesArray.setPosition(position);
    }

    @Override
    public void flush() {
        bytesArray.flush();
    }

    @Override
    public void clear() {

        if (bytesArray != null) {
            bytesArray.clear();
            bytesArray = null;
        }
    }

    @Override
    public void forceRelease() {
        bytesArray.forceRelease();
    }

    @Override
    public boolean isForceReleased() {
        return bytesArray.isForceReleased();
    }
}

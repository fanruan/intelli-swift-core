package com.finebi.cube.data.disk.writer;

import com.finebi.cube.BICubeLongTypePosition;
import com.finebi.cube.data.output.primitive.ICubeByteWriter;
import com.finebi.cube.data.output.primitive.ICubeLongWriter;
import com.finebi.cube.data.output.ICubeByteArrayWriter;
import com.finebi.cube.data.output.primitive.ICubeIntegerWriter;
import com.fr.bi.stable.constant.CubeConstant;

/**
 * Created by 小灰灰 on 14-1-7.
 */
public class BIByteArrayNIOWriter implements ICubeByteArrayWriter {
    protected ICubeLongWriter startPositionRecorder;

    protected ICubeIntegerWriter lengthRecorder;

    protected ICubeByteWriter contentRecorder;

    protected long pos = 0;


    public BIByteArrayNIOWriter(ICubeLongWriter startPositionRecorder, ICubeIntegerWriter lengthRecorder, ICubeByteWriter contentRecorder) {
        this.startPositionRecorder = startPositionRecorder;
        this.lengthRecorder = lengthRecorder;
        this.contentRecorder = contentRecorder;
    }

    @Override
    public void recordSpecificValue(int specificPosition, byte[] v) {
        if (v == null) {
            v = CubeConstant.NULLBYTES;
        }
        int len = v.length;
        long start = pos;
        startPositionRecorder.recordSpecificPositionValue(specificPosition, new Long(start));
        lengthRecorder.recordSpecificPositionValue(specificPosition, new Integer(len));
        for (int i = 0; i < len; i++) {
            contentRecorder.recordSpecificPositionValue(start + i, v[i]);
        }
        pos += len;
    }

    @Override
    public void setPosition(BICubeLongTypePosition position) {

    }

    @Override
    public void flush() {
        startPositionRecorder.flush();
        lengthRecorder.flush();
        contentRecorder.flush();
    }

    @Override
    public void clear() {
        startPositionRecorder.clear();
        lengthRecorder.clear();
        contentRecorder.clear();
    }

    @Override
    public void forceRelease() {
        startPositionRecorder.forceRelease();
        lengthRecorder.forceRelease();
        contentRecorder.forceRelease();
    }

    @Override
    public boolean isForceReleased() {
        return startPositionRecorder.isForceReleased() ||
                lengthRecorder.isForceReleased() ||
                contentRecorder.isForceReleased();
    }

    @Override
    public void saveStatus() {

    }
}
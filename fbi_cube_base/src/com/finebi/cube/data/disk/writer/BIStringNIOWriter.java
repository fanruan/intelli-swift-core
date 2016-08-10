package com.finebi.cube.data.disk.writer;

import com.finebi.cube.BICubeLongTypePosition;
import com.finebi.cube.data.output.ICubeByteArrayWriter;
import com.finebi.cube.data.output.ICubeStringWriter;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.stable.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by 小灰灰 on 2014/5/12.
 */
public class BIStringNIOWriter implements ICubeStringWriter {
    private ICubeByteArrayWriter byteWriteMappedList;

    public BIStringNIOWriter(ICubeByteArrayWriter byteWriteMappedList) {
        this.byteWriteMappedList = byteWriteMappedList;
    }

    @Override
    public void recordSpecificValue(int specificPosition, String v) {
        byte[] b = null;
        //pony string不需要区分null，只要空
        String value = v == null ? StringUtils.EMPTY : v;
        try {
            b = value.getBytes(CubeConstant.CODE);
        } catch (UnsupportedEncodingException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        byteWriteMappedList.recordSpecificValue(specificPosition, b);
    }

    @Override
    public void clear() {
        if (byteWriteMappedList != null) {
            byteWriteMappedList.clear();
            byteWriteMappedList = null;
        }
    }

    @Override
    public void flush() {
        byteWriteMappedList.flush();
    }

    @Override
    public void setPosition(BICubeLongTypePosition position) {
        byteWriteMappedList.setPosition(position);
    }


    @Override
    public void saveStatus() {
    }

    @Override
    public void forceRelease() {
        byteWriteMappedList.forceRelease();
    }

    @Override
    public boolean isForceReleased() {
        return byteWriteMappedList.isForceReleased();
    }
}
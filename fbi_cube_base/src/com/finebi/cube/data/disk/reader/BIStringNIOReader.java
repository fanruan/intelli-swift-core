package com.finebi.cube.data.disk.reader;

import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.data.input.ICubeByteArrayReader;
import com.finebi.cube.data.input.ICubeStringReader;
import com.fr.bi.stable.constant.CubeConstant;

import java.io.UnsupportedEncodingException;

public class BIStringNIOReader implements ICubeStringReader {

    private ICubeByteArrayReader byteArrayReader;
    private transient long tempRow = Long.MIN_VALUE;
    private transient String tempValue;

    public BIStringNIOReader(ICubeByteArrayReader byteArrayReader) {
        this.byteArrayReader = byteArrayReader;
    }

    @Override
    public String getSpecificValue(int rowNumber) throws BIResourceInvalidException {
        if (rowNumber == tempRow) {
            return tempValue;
        }
        byte[] by = byteArrayReader.getSpecificValue(rowNumber);
        String result = null;
        if (by == null) {
        } else {
            try {
                result = new String(by, CubeConstant.CODE);
            } catch (UnsupportedEncodingException e) {
                result = new String(by);
            }
        }
        tempValue = result;
        tempRow = rowNumber;
        return result;
    }


    @Override
    public long getLastPosition(long row) {
        return byteArrayReader.getLastPosition(row);
    }

    @Override
    public void clear() {
        if (byteArrayReader != null) {
            byteArrayReader.clear();
            byteArrayReader = null;
        }
    }

    @Override
    public boolean canRead() {
        return byteArrayReader.canRead();
    }
}
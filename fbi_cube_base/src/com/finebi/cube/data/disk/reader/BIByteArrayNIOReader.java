package com.finebi.cube.data.disk.reader;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.input.ICubeByteArrayReader;
import com.finebi.cube.data.input.primitive.ICubeByteReader;
import com.finebi.cube.data.input.primitive.ICubeIntegerReader;
import com.finebi.cube.data.input.primitive.ICubeLongReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.gvi.BIByteDataInput;

import java.util.UUID;

public class BIByteArrayNIOReader implements ICubeByteArrayReader, Release {
    private final String handlerKey = UUID.randomUUID().toString();
    private ICubeLongReader positionReader;

    private ICubeIntegerReader lengthReader;

    private ICubeByteReader contentReader;

    private static BILogger LOGGER = BILoggerFactory.getLogger(BIByteArrayNIOReader.class);

    public BIByteArrayNIOReader(ICubeLongReader positionReader, ICubeIntegerReader lengthReader, ICubeByteReader contentReader) {
        this.positionReader = positionReader;
        this.lengthReader = lengthReader;
        this.contentReader = contentReader;
        this.positionReader.getHandlerReleaseHelper().registerHandlerKey(handlerKey);
        this.lengthReader.getHandlerReleaseHelper().registerHandlerKey(handlerKey);
        this.contentReader.getHandlerReleaseHelper().registerHandlerKey(handlerKey);
    }


    @Override
    public byte[] getSpecificValue(final int row) throws BIResourceInvalidException {
        long start = 0;
        int size = 0;
        try {
            start = positionReader.getSpecificValue(row);
            size = lengthReader.getSpecificValue(row);
        } catch (Exception e) {
            LOGGER.warnCache(e.getMessage()+" retry again!");
            start = positionReader.getSpecificValue(row);
            size = lengthReader.getSpecificValue(row);
        }
        if (size == 0) {
            return new byte[]{};
        }
        byte[] b = new byte[size];
        for (int i = 0; i < size; i++) {
            b[i] = contentReader.getSpecificValue(start + i);
        }
        return isNull(b) ? null : b;
    }

    public BIByteDataInput getByteStream(int row) throws BIResourceInvalidException {
        long start;
        int size;
        try {
            start = positionReader.getSpecificValue(row);
            size = lengthReader.getSpecificValue(row);
        } catch (Exception e) {
            LOGGER.warnCache(e.getMessage() + " retry again! The invalid reader is lengthReader or positionReader");
            start = positionReader.getSpecificValue(row);
            size = lengthReader.getSpecificValue(row);
        }
        return new ByteStreamDataInput(contentReader, start, size);
    }

    private boolean isNull(byte[] result) {
        if (result.length == CubeConstant.NULLBYTES.length) {
            for (int i = 0; i < result.length; i++) {
                if (result[i] != CubeConstant.NULLBYTES[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public long getLastPosition(long row) {
        if (row == 0) {
            return 0;
        }
        long start = 0;
        int size = 0;
        try {
            start = positionReader.getSpecificValue(row - 1);
        } catch (BIResourceInvalidException e) {
            LOGGER.errorCache("getLastPosition positionReader BIResourceInvalidException ", e);
        }
        try {
            size = lengthReader.getSpecificValue(row - 1);
        } catch (BIResourceInvalidException e) {
            LOGGER.errorCache("getLastPosition lengthReader BIResourceInvalidException ", e);
        }
        return start + size;
    }

    @Override
    public void clear() {
        positionReader.releaseHandler(handlerKey);
        lengthReader.releaseHandler(handlerKey);
        contentReader.releaseHandler(handlerKey);
    }

    @Override
    public boolean canRead() {
        return contentReader.canReader();
    }

    @Override
    public void forceRelease() {
        positionReader.forceRelease();
        lengthReader.forceRelease();
        contentReader.forceRelease();
    }

    @Override
    public boolean isForceReleased() {
        return positionReader.isForceReleased() ||
                lengthReader.isForceReleased() ||
                contentReader.isForceReleased();
    }
}
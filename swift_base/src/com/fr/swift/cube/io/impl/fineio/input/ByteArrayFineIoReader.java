package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.cube.io.input.ByteArrayReader;
import com.fr.swift.cube.io.input.ByteDataInput;
import com.fr.swift.cube.io.input.ByteReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.location.IResourceLocation;

/**
 * @author anchore
 */
public class ByteArrayFineIoReader extends BaseFineIoReader implements ByteArrayReader {

    private ByteReader contentReader;
    private LongReader positionReader;
    private IntReader lengthReader;

    private ByteArrayFineIoReader(ByteReader contentReader, LongReader positionReader, IntReader lengthReader) {
        this.contentReader = contentReader;
        this.positionReader = positionReader;
        this.lengthReader = lengthReader;
    }

    public static ByteArrayReader build(IResourceLocation location) {
        // 获得内容部分的byte类型reader
        IResourceLocation contentLocation = location.buildChildLocation(CONTENT);
        ByteReader contentReader = ByteFineIoReader.build(contentLocation);

        // 获得位置部分的long类型reader
        IResourceLocation positionLocation = location.buildChildLocation(POSITION);
        LongReader positionReader = LongFineIoReader.build(positionLocation);

        // 获得长度部分的int类型reader
        IResourceLocation lengthLocation = location.buildChildLocation(LENGTH);
        IntReader lengthReader = IntFineIoReader.build(lengthLocation);

        return new ByteArrayFineIoReader(contentReader, positionReader, lengthReader);
    }

    @Override
    public boolean isReadable() {
        return contentReader != null && contentReader.isReadable();
    }

    @Override
    public long getLastPosition(long pos) {
        if (pos == 0) {
            return 0;
        }
        long start = positionReader.get(pos - 1);
        int len = lengthReader.get(pos - 1);
        return start + len;
    }

    @Override
    public byte[] get(long pos) {
        long start = positionReader.get(pos);
        int size = lengthReader.get(pos);
        if (size == 0) {
            return NULL_VALUE;
        }
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = contentReader.get(start + i);
        }
        return bytes;
    }

    @Override
    public ByteDataInput getByteStream(long pos) {
        long start = positionReader.get(pos);
        long size = lengthReader.get(pos);
        return new ByteDataInputStream(contentReader, start, size);
    }

}

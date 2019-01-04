package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.cube.io.input.ByteArrayReader;
import com.fr.swift.cube.io.input.ByteReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.util.IoUtil;

import java.net.URI;

/**
 * @author anchore
 */
public class ByteArrayFineIoReader implements ByteArrayReader {
    private ByteReader contentReader;
    private LongReader positionReader;
    private IntReader lengthReader;

    private ByteArrayFineIoReader(ByteReader contentReader, LongReader positionReader, IntReader lengthReader) {
        this.contentReader = contentReader;
        this.positionReader = positionReader;
        this.lengthReader = lengthReader;
    }

    public static ByteArrayReader build(URI location) {
        // 获得内容部分的byte类型reader
        URI contentLocation = URI.create(location.getPath() + "/" + CONTENT);
        ByteReader contentReader = ByteFineIoReader.build(contentLocation);

        // 获得位置部分的long类型reader
        URI positionLocation = URI.create(location.getPath() + "/" + POSITION);
        LongReader positionReader = LongFineIoReader.build(positionLocation);

        // 获得长度部分的int类型reader
        URI lengthLocation = URI.create(location.getPath() + "/" + LENGTH);
        IntReader lengthReader = IntFineIoReader.build(lengthLocation);

        return new ByteArrayFineIoReader(contentReader, positionReader, lengthReader);
    }

    @Override
    public boolean isReadable() {
        return contentReader != null && contentReader.isReadable();
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
    public void release() {
        IoUtil.release(contentReader, lengthReader, positionReader);
    }
}
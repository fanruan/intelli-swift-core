package com.fr.swift.cube.io.impl;

import com.fr.swift.cube.io.input.ByteArrayReader;
import com.fr.swift.cube.io.input.ByteReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.util.IoUtil;

import java.io.InputStream;

/**
 * @author anchore
 * @date 2019/3/25
 */
public class BaseByteArrayReader implements ByteArrayReader {

    private ByteReader dataReader;

    private LongReader posReader;

    private IntReader lenReader;

    public BaseByteArrayReader(ByteReader dataReader, LongReader posReader, IntReader lenReader) {
        this.dataReader = dataReader;
        this.posReader = posReader;
        this.lenReader = lenReader;
    }

    @Override
    public boolean isReadable() {
        return dataReader != null && dataReader.isReadable()
                && posReader != null && posReader.isReadable()
                && lenReader != null && lenReader.isReadable();
    }

    @Override
    public byte[] get(long pos) {
        long start = posReader.get(pos);
        int size = lenReader.get(pos);
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = dataReader.get(start + i);
        }
        return bytes;
    }

    @Override
    public InputStream getStream(long pos) {
        final long start = posReader.get(pos);
        final int size = lenReader.get(pos);

        return new InputStream() {
            long cursor = start;

            @Override
            public int read() {
                if (available() > 0) {
                    final int b = dataReader.get(cursor) & 0xFF;
                    cursor++;
                    return b;
                }
                return -1;
            }

            @Override
            public int available() {
                return (int) (start + size - cursor);
            }
        };
    }

    @Override
    public void release() {
        IoUtil.release(dataReader, lenReader, posReader);
    }
}
package com.fr.swift.cube.nio.read;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.cube.nio.NIOReader;

import java.io.File;

public class ByteReadMappedList implements NIOReader<byte[]>, Releasable {

    private LongNIOReader index_array;

    private IntNIOReader size_array;

    private ByteNIOReader byteList;

    public ByteReadMappedList(String path) {
        index_array = new LongNIOReader(new File(path + "_index"));
        size_array = new IntNIOReader(new File(path + "_size"));
        byteList = new ByteNIOReader(new File(path));
    }


    public ByteReadMappedList(File f) {
        this(f.getAbsolutePath());
    }

    @Override
    public byte[] get(final long row) {
        long start = index_array.get(row);
        int size = size_array.get(row);
        if (size == 0) {
            return new byte[]{};
        }
        byte[] b = new byte[size];
        for (int i = 0; i < size; i++) {
            b[i] = byteList.get(start + i);
        }
        return b;
    }

    @Override
    public long getLastPos(long row) {
        if (row == 0) {
            return 0;
        }
        long start = index_array.get(row - 1);
        int size = size_array.get(row - 1);
        return start + size;
    }

    @Override
    public void release() {
        index_array.release();
        size_array.release();
        byteList.release();
    }

}
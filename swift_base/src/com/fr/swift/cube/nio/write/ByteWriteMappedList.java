package com.fr.swift.cube.nio.write;

import com.fr.swift.cube.io.output.ByteArrayWriter;
import com.fr.swift.cube.nio.NIOWriter;

import java.io.File;

/**
 * Created by 小灰灰 on 14-1-7.
 */
public class ByteWriteMappedList implements NIOWriter<byte[]> {
    protected LongNIOWriter index_array;

    protected IntNIOWriter size_array;

    protected ByteNIOWriter byteList;

    protected long pos = 0;

    public ByteWriteMappedList(String path) {
        index_array = new LongNIOWriter(new File(path + "_index"));
        size_array = new IntNIOWriter(new File(path + "_size"));
        byteList = new ByteNIOWriter(new File(path));
    }


    @Override
    public void add(long row, byte[] v) {
        if (v == null) {
            v = ByteArrayWriter.NULL_VALUE;
        }
        int len = v.length;
        long start = pos;
        index_array.add(row, new Long(start));
        size_array.add(row, new Integer(len));
        for (int i = 0; i < len; i++) {
            byteList.add(start + i, v[i]);
        }
        pos += len;
    }

    @Override
    public void setPos(long p) {
        pos = p;
    }

    @Override
    public void release() {
        index_array.release();
        size_array.release();
        byteList.release();
    }

    @Override
    public void save() {
        index_array.save();
        size_array.save();
        byteList.save();
    }
}
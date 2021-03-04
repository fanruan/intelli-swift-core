package com.fr.swift.cloud.cube.nio.write;

import com.fr.swift.cloud.cube.nio.NIOWriter;
import com.fr.swift.cloud.util.IoUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by 小灰灰 on 2014/5/12.
 */
public class StringWriteMappedList implements NIOWriter<String> {
    private NIOWriter<byte[]> byteWriteMappedList;

    public StringWriteMappedList(String path) {
        byteWriteMappedList = new ByteWriteMappedList(path);
    }

    @Override
    public void add(long row, String v) {
        byte[] b = null;
        if (v != null) {
            String value = v;
            try {
                b = value.getBytes("utf8");

            } catch (UnsupportedEncodingException e) {
            }

        }
        byteWriteMappedList.add(row, b);
    }

    @Override
    public void release() {
        IoUtil.release(byteWriteMappedList);
        byteWriteMappedList = null;
    }

    @Override
    public void setPos(long pos) {
        byteWriteMappedList.setPos(pos);
    }

    @Override
    public void save() {
    }
}
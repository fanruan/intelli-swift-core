package com.fr.swift.cube.nio.read;

import com.fr.swift.cube.nio.NIOReader;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class StringReadMappedList implements NIOReader<String> {

    private NIOReader<byte[]> byteNIOReader;
    private transient long tempRow = -1;
    private transient String tempValue;

    public StringReadMappedList(String path) {
        byteNIOReader = new ByteReadMappedList(path);
    }

    public StringReadMappedList(File f) {
        byteNIOReader = new ByteReadMappedList(f.getAbsolutePath());
    }

    @Override
    public String get(long row) {
        if (row == tempRow) {
            return tempValue;
        }
        byte[] by = byteNIOReader.get(row);
        String result = null;
        if (by == null) {
        } else {
            try {
                result = new String(by, "utf8");
            } catch (UnsupportedEncodingException e) {
                result = new String(by);
            }
        }
        tempValue = result;
        tempRow = row;
        return result;
    }

    public int getOffSet() {
        return 0;
    }


    @Override
    public long getLastPos(long row) {
        return byteNIOReader.getLastPos(row);
    }

    @Override
    public void release() {
        if (byteNIOReader != null) {
            byteNIOReader.release();
            byteNIOReader = null;
        }
    }

    public void delete() {
        release();
    }
}
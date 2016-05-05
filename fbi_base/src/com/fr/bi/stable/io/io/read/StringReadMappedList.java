package com.fr.bi.stable.io.io.read;

import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.io.newio.NIOReader;

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
                result = new String(by, CubeConstant.CODE);
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
	public long getLastPos(long row){
        return byteNIOReader.getLastPos(row);
    }

    @Override
    public void releaseResource() {
        if (byteNIOReader != null) {
            byteNIOReader.releaseResource();
            byteNIOReader = null;
        }
    }

    public void delete() {
        releaseResource();
    }
}
package com.fr.bi.stable.io.io.write;

import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.io.newio.NIOWriter;

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
                b = value.getBytes(CubeConstant.CODE);

            } catch (UnsupportedEncodingException e) {
            }

        }
        byteWriteMappedList.add(row, b);
    }

    @Override
    public void clear() {
        if (byteWriteMappedList != null) {
            byteWriteMappedList.clear();
            byteWriteMappedList = null;
        }
    }

    @Override
	public void setPos(long pos){
        byteWriteMappedList.setPos(pos);
    };

    @Override
    public void save() {
    }
}
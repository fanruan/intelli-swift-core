package com.fr.swift.io.nio;

import com.fr.swift.io.SameObjectIo;
import com.fr.swift.util.IoUtil;

import java.nio.charset.Charset;

/**
 * @author anchore
 * @date 2018/7/20
 */
public class StringNio extends BaseNio implements SameObjectIo<String> {
    private SameObjectIo<byte[]> strings;

    public StringNio(String basePath) {
        this(basePath, BaseAtomNio.PAGE_SIZE);
    }

    public StringNio(String basePath, int pageSize) {
        super(basePath);
        strings = ByteArrayNio.of(basePath, pageSize);
    }

    public static SameObjectIo<String> of(String basePath) {
        return new StringNio(basePath);
    }

    public static SameObjectIo<String> of(String basePath, int pageSize) {
        return new StringNio(basePath, pageSize);
    }


    @Override
    public void flush() {

    }

    @Override
    public String get(long pos) {
        return new String(strings.get(pos), Charset.forName("utf8"));
    }

    @Override
    public long getLastPosition(long pos) {
        return 0;
    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public void put(long pos, String val) {
        strings.put(pos, val.getBytes(Charset.forName("utf8")));
    }

    @Override
    public void release() {
        IoUtil.release(strings);
        strings = null;
    }
}
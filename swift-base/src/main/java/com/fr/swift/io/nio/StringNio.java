package com.fr.swift.io.nio;

import com.fr.swift.cube.io.input.StringReader;
import com.fr.swift.cube.io.output.StringWriter;
import com.fr.swift.io.ObjectIo;
import com.fr.swift.util.IoUtil;

import java.nio.charset.Charset;

/**
 * @author anchore
 * @date 2018/7/20
 */
public class StringNio extends BaseNio implements StringWriter, StringReader, ObjectIo<String> {
    private ObjectIo<byte[]> obj;

    public StringNio(NioConf conf) {
        super(conf);
        obj = new ByteArrayNio(conf);
    }

    @Override
    public void flush() {

    }

    @Override
    public String get(long pos) {
        return new String(obj.get(pos), Charset.forName("utf8"));
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
        obj.put(pos, val.getBytes(Charset.forName("utf8")));
    }

    @Override
    public void release() {
        IoUtil.release(obj);
        obj = null;
    }
}
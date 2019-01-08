package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.cube.io.ObjectIo;
import com.fr.swift.cube.io.input.StringReader;
import com.fr.swift.cube.io.output.StringWriter;
import com.fr.swift.util.IoUtil;

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
        return new String(obj.get(pos), StringReader.CHARSET);
    }

    @Override
    public boolean isReadable() {
        return obj != null && obj.isReadable();
    }

    @Override
    public void put(long pos, String val) {
        obj.put(pos, val.getBytes(StringWriter.CHARSET));
    }

    @Override
    public void release() {
        IoUtil.release(obj);
        obj = null;
    }
}
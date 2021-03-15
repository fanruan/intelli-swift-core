package com.fr.swift.cloud.cube.io.impl.nio;

import com.fr.swift.cloud.cube.io.ObjectIo;
import com.fr.swift.cloud.cube.io.impl.BaseStringReader;
import com.fr.swift.cloud.cube.io.impl.BaseStringWriter;
import com.fr.swift.cloud.cube.io.input.StringReader;
import com.fr.swift.cloud.cube.io.output.StringWriter;
import com.fr.swift.cloud.util.IoUtil;

/**
 * @author anchore
 * @date 2018/7/20
 */
public class StringNio implements StringWriter, StringReader, ObjectIo<String> {
    private StringWriter stringWriter;
    private StringReader stringReader;

    public StringNio(NioConf conf) {
        ByteArrayNio byteArrayNio = new ByteArrayNio(conf);
        stringWriter = new BaseStringWriter(byteArrayNio);
        stringReader = new BaseStringReader(byteArrayNio);
    }

    @Override
    public void put(long pos, String val) {
        stringWriter.put(pos, val);
    }

    @Override
    public boolean isReadable() {
        return stringReader != null && stringReader.isReadable();
    }

    @Override
    public String get(long pos) {
        return stringReader.get(pos);
    }

    @Override
    public void release() {
        IoUtil.release(stringWriter, stringReader);
    }
}
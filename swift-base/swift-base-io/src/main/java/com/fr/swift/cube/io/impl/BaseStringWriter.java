package com.fr.swift.cube.io.impl;

import com.fr.swift.cube.io.output.ByteArrayWriter;
import com.fr.swift.cube.io.output.StringWriter;
import com.fr.swift.util.IoUtil;

/**
 * @author anchore
 * @date 2019/4/4
 */
public class BaseStringWriter implements StringWriter {
    private ByteArrayWriter byteArrayWriter;

    public BaseStringWriter(ByteArrayWriter byteArrayWriter) {
        this.byteArrayWriter = byteArrayWriter;
    }

    @Override
    public void put(long pos, String val) {
        byteArrayWriter.put(pos, val.getBytes(StringWriter.CHARSET));
    }

    @Override
    public void release() {
        IoUtil.release(byteArrayWriter);
    }
}
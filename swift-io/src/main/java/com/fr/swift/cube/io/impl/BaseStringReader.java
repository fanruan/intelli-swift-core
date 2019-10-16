package com.fr.swift.cube.io.impl;

import com.fr.swift.cube.io.input.ByteArrayReader;
import com.fr.swift.cube.io.input.StringReader;
import com.fr.swift.cube.io.output.StringWriter;
import com.fr.swift.util.IoUtil;

/**
 * @author anchore
 * @date 2019/4/4
 */
public class BaseStringReader implements StringReader {
    private ByteArrayReader byteArrayReader;

    public BaseStringReader(ByteArrayReader byteArrayReader) {
        this.byteArrayReader = byteArrayReader;
    }

    @Override
    public boolean isReadable() {
        return byteArrayReader != null && byteArrayReader.isReadable();
    }

    @Override
    public String get(long pos) {
        return new String(byteArrayReader.get(pos), StringWriter.CHARSET);
    }

    @Override
    public void release() {
        IoUtil.release(byteArrayReader);
    }
}
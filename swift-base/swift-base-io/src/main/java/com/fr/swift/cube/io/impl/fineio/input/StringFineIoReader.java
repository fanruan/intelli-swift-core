package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.cube.io.input.ByteArrayReader;
import com.fr.swift.cube.io.input.StringReader;
import com.fr.swift.util.IoUtil;

import java.net.URI;

/**
 * @author anchore
 */
public class StringFineIoReader implements StringReader {
    private ByteArrayReader bar;
    private long tempRow = -1;
    private String tempValue;

    private StringFineIoReader(ByteArrayReader bar) {
        this.bar = bar;
    }

    public static StringReader build(URI location) {
        // 底层为byte array reader
        return new StringFineIoReader(ByteArrayFineIoReader.build(location));
    }

    @Override
    public String get(long pos) {
        if (pos == tempRow) {
            return tempValue;
        }
        byte[] bytes = bar.get(pos);
        tempValue = new String(bytes, CHARSET);
        tempRow = pos;
        return tempValue;
    }

    @Override
    public boolean isReadable() {
        return bar.isReadable();
    }

    @Override
    public void release() {
        IoUtil.release(bar);
    }
}

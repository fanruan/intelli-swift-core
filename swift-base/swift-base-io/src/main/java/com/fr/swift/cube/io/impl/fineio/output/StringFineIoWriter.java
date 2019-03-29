package com.fr.swift.cube.io.impl.fineio.output;

import com.fr.swift.cube.io.output.ByteArrayWriter;
import com.fr.swift.cube.io.output.StringWriter;
import com.fr.swift.util.IoUtil;

import java.net.URI;

/**
 * @author anchore
 */
public class StringFineIoWriter implements StringWriter {
    private ByteArrayWriter baw;

    private StringFineIoWriter(ByteArrayWriter baw) {
        this.baw = baw;
    }

    public static StringWriter build(URI location, boolean isOverwrite) {
        return new StringFineIoWriter(ByteArrayFineIoWriter.build(location, isOverwrite));
    }

    @Override
    public void release() {
        IoUtil.release(baw);
    }

    @Override
    public void put(long pos, String val) {
        baw.put(pos, val == null ? null : val.getBytes(CHARSET));
    }
}
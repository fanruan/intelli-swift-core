package com.fr.swift.cube.io.output;

import java.io.OutputStream;

/**
 * @author anchore
 */
public interface ByteArrayWriter extends ObjectWriter<byte[]> {
    String CONTENT = "content";
    String POSITION = "position";
    String LENGTH = "length";
    String LAST_POSITION = "last_position";

    /**
     * todo 要去掉的，这个接口不好
     */
    void resetContentPosition();

    OutputStream putStream(long pos);
}
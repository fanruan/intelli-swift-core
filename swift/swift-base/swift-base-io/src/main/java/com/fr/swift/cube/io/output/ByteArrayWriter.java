package com.fr.swift.cube.io.output;

/**
 * @author anchore
 */
public interface ByteArrayWriter extends ObjectWriter<byte[]> {
    String CONTENT = "content";
    String POSITION = "position";
    String LENGTH = "length";
    String LAST_POSITION = "last_position";

    byte[] NULL_VALUE = new byte[0];

    /**
     * todo 要去掉的，这个接口不好
     */
    void resetContentPosition();
}
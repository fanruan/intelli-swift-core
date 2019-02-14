package com.fr.swift.cube.io.input;

import com.fr.swift.cube.io.output.ByteArrayWriter;

/**
 * @author anchore
 */
public interface ByteArrayReader extends ObjectReader<byte[]> {
    String CONTENT = ByteArrayWriter.CONTENT;
    String POSITION = ByteArrayWriter.POSITION;
    String LENGTH = ByteArrayWriter.LENGTH;

    byte[] NULL_VALUE = ByteArrayWriter.NULL_VALUE;
}
package com.fr.swift.cloud.cube.io.input;

import java.io.InputStream;

/**
 * @author anchore
 */
public interface ByteArrayReader extends ObjectReader<byte[]> {

    InputStream getStream(long pos);
}
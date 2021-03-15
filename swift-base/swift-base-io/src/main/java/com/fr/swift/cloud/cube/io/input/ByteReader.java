package com.fr.swift.cloud.cube.io.input;

/**
 * @author anchore
 */
public interface ByteReader extends PrimitiveReader {

    byte get(long pos);

}

package com.fr.swift.cube.io.input;

/**
 * @author anchore
 */
public interface ByteReader extends PrimitiveReader {

    byte get(long pos);

}

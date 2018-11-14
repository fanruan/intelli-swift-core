package com.fr.swift.cube.io.output;

/**
 * @author anchore
 */
public interface ByteWriter extends PrimitiveWriter {

    void put(long pos, byte val);

}

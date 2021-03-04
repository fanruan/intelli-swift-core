package com.fr.swift.cloud.cube.io.input;

/**
 * @author anchore
 */
public interface DoubleReader extends PrimitiveReader {

    double get(long pos);

}

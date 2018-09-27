package com.fr.swift.cube.io.output;

/**
 * @author anchore
 */
public interface DoubleWriter extends PrimitiveWriter {
    void put(long pos, double val);
}

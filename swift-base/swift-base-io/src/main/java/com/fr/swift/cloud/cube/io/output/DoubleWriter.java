package com.fr.swift.cloud.cube.io.output;

/**
 * @author anchore
 */
public interface DoubleWriter extends PrimitiveWriter {
    void put(long pos, double val);
}

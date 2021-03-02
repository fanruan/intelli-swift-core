package com.fr.swift.cloud.cube.io.output;

/**
 * @author anchore
 */
public interface IntWriter extends PrimitiveWriter {
    void put(long pos, int val);
}
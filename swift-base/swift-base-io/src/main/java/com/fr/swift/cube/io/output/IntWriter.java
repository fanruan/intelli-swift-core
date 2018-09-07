package com.fr.swift.cube.io.output;

/**
 * @author anchore
 */
public interface IntWriter extends PrimitiveWriter {
    void put(long pos, int val);
}
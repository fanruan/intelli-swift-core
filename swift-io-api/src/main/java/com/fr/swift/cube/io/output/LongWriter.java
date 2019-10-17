package com.fr.swift.cube.io.output;

/**
 * @author anchore
 */
public interface LongWriter extends PrimitiveWriter {
    void put(long pos, long val);
}
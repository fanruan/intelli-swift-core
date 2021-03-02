package com.fr.swift.cloud.cube.io.output;

/**
 * @author anchore
 */
public interface ObjectWriter<T> extends Writer {
    void put(long pos, T val);
}
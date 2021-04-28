package com.fr.swift.cloud.cube.io.input;

/**
 * @author anchore
 */
public interface ObjectReader<T> extends Reader {
    T get(long pos);
}
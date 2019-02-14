package com.fr.swift.cube.io.input;

/**
 * @author anchore
 */
public interface ObjectReader<T> extends Reader {
    T get(long pos);
}
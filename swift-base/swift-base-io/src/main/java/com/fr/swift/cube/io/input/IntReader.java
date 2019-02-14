package com.fr.swift.cube.io.input;

/**
 * @author anchore
 */
public interface IntReader extends PrimitiveReader {
    int get(long pos);
}
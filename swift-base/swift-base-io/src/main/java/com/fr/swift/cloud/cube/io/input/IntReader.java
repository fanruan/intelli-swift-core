package com.fr.swift.cloud.cube.io.input;

/**
 * @author anchore
 */
public interface IntReader extends PrimitiveReader {
    int get(long pos);
}
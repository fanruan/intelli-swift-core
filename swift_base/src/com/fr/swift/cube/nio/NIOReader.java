package com.fr.swift.cube.nio;

public interface NIOReader<T> extends NIO {

    T get(long row);

    long getLastPos(long rowCount);

}
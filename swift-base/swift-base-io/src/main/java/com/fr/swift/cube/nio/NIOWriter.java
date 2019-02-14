package com.fr.swift.cube.nio;


public interface NIOWriter<T> extends NIO {

    void add(long row, T value);

    void save();

    void setPos(long pos);
}
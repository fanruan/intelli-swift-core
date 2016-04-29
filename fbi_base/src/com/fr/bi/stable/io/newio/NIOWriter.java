package com.fr.bi.stable.io.newio;


public interface NIOWriter<T> extends NIO {

    public void add(long row, T value);

    public void save();

    public void setPos(long pos);
}
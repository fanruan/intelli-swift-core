package com.fr.bi.stable.io.newio;

public interface NIOReader<T> extends NIO {

     T get(long row);

     long getLastPos(long rowCount);

}
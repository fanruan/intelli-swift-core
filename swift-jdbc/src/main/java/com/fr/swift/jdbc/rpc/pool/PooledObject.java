package com.fr.swift.jdbc.rpc.pool;

import java.io.PrintWriter;
import java.util.Deque;

public interface PooledObject<T> extends Comparable<PooledObject<T>> {

    T getObject();

    long getCreateTime();

    long getActiveTimeMillis();

    long getIdleTimeMillis();

    long getLastBorrowTime();

    long getLastReturnTime();

    long getLastUsedTime();

    @Override
    int compareTo(PooledObject<T> other);

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    @Override
    String toString();

    boolean startEvictionTest();

    boolean endEvictionTest(Deque<PooledObject<T>> idleQueue);

    boolean allocate();

    boolean deallocate();

    void invalidate();

    void setLogAbandoned(boolean logAbandoned);

    void use();

    void printStackTrace(PrintWriter writer);

    PooledObjectState getState();

    void markAbandoned();

    void markReturning();

}

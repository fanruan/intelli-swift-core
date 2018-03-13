package com.fr.swift.structure.queue;

/**
 * Created by Lyon on 2018/3/6.
 *
 * todo 其实java.util.Queue就满足fifo，不用再写个接口
 */
public interface FIFOQueue<T> {

    void add(T item);

    T remove();

    boolean isEmpty();
}

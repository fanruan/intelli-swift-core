package com.fr.swift.util.concurrent;

/**
 * @author anchore
 * @date 2018/5/5
 * <p>
 * 单个线程的factory
 */
public class SingleThreadFactory extends BaseThreadFactory {
    public SingleThreadFactory(String namePrefix) {
        super(namePrefix);
    }

    public SingleThreadFactory(Class<?> c) {
        super(c);
    }
}
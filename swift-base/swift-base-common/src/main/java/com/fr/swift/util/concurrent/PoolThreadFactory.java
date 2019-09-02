package com.fr.swift.util.concurrent;

/**
 * @author anchore
 * @date 2018/4/27
 * <p>
 * 线程池线程的factory
 */
public class PoolThreadFactory extends BaseThreadFactory {

    public PoolThreadFactory(String poolPrefix) {
        super(String.format("%s-thread", poolPrefix));
    }

    public PoolThreadFactory(Class<?> c) {
        this(getSimpleName(c));
    }
}
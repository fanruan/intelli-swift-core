package com.fr.swift.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author anchore
 * @date 2018/4/27
 * <p>
 * 线程池线程的factory
 */
public class PoolThreadFactory extends BaseThreadFactory {
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

    public PoolThreadFactory(String poolPrefix) {
        super(String.format("%s-%d-thread", poolPrefix, POOL_NUMBER.getAndIncrement()));
    }

    public PoolThreadFactory(Class<?> c) {
        this(getSimpleName(c));
    }
}
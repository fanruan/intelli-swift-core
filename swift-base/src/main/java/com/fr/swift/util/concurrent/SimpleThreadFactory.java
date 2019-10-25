package com.fr.swift.util.concurrent;

/**
 * @author anchore
 * @date 2018/5/5
 * <p>
 * 普通线程的factory
 */
public class SimpleThreadFactory extends BaseThreadFactory {
    public SimpleThreadFactory(String namePrefix) {
        super(namePrefix);
    }

    public SimpleThreadFactory(Class<?> c) {
        super(c);
    }
}
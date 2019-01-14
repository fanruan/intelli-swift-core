package com.fr.swift.jdbc.rpc.serializable.clazz;

import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2019-01-11
 */
public class CachingClassResolverTest {

    @Test
    public void resolve() throws ClassNotFoundException {
        ClassResolver delegate = new ClassLoaderClassResolver(CachingClassResolverTest.class.getClassLoader());
        ClassResolver resolver = new CachingClassResolver(delegate, new ConcurrentHashMap<String, Class<?>>());
        long start = System.nanoTime();
        resolver.resolve("com.fr.swift.jdbc.rpc.serializable.clazz.CachingClassResolverTest");
        long first = System.nanoTime() - start;
        System.out.println(first);
        start = System.nanoTime();
        resolver.resolve("com.fr.swift.jdbc.rpc.serializable.clazz.CachingClassResolverTest");
        long cached = System.nanoTime() - start;
        System.out.println(cached);
        assertTrue(cached <= first);
    }
}
package com.fr.swift.executor.task.netty.protocol.serilizer;


import com.fr.swift.executor.task.netty.protocol.serilizer.impl.JSONSerilizer;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public interface Serilizer {

    Serilizer DEFAULT = new JSONSerilizer();

    byte[] serilize(Object object);

    <T> T deSerilize(byte[] bytes, Class<T> clazz);

}

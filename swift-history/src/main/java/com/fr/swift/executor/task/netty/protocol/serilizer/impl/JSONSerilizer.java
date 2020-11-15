package com.fr.swift.executor.task.netty.protocol.serilizer.impl;


import com.alibaba.fastjson.JSON;
import com.fr.swift.executor.task.netty.protocol.serilizer.Serilizer;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class JSONSerilizer implements Serilizer {

    @Override
    public byte[] serilize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deSerilize(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz);
    }
}

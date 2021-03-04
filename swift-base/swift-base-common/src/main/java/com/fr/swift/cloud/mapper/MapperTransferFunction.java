package com.fr.swift.cloud.mapper;

/**
 * @author Heng.J
 * @date 2020/6/17
 * @description
 * @since swift 1.1
 */
public interface MapperTransferFunction<T> {

    T transfer(Object value, String[] paramValue);
}

package com.fr.swift.mapper;

/**
 * @author Heng.J
 * @date 2020/6/17
 * @description
 * @since swift 1.1
 */
public interface MapperTransferFunc<T> {

    T transfer(Object value);
}

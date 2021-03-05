package com.fr.swift.cloud.segment.operator;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/11/18
 */
public interface RollBackable<T, S> {

    S snapshot(T t);

    void rollback();
}
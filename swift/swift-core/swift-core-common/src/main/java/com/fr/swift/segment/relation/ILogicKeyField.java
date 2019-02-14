package com.fr.swift.segment.relation;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/16
 */
public interface ILogicKeyField<T, F> {
    T belongTo();

    List<F> getKeyFields();

    String getFieldName();
}

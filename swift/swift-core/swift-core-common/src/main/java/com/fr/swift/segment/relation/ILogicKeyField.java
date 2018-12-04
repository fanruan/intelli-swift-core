package com.fr.swift.segment.relation;

import java.io.Serializable;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/16
 */
public interface ILogicKeyField<T, F> extends Serializable {
    T belongTo();

    List<F> getKeyFields();

    String getFieldName();
}

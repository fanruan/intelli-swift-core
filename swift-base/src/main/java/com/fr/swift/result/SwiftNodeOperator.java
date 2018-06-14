package com.fr.swift.result;

/**
 * Created by Lyon on 2018/6/12.
 */
public interface SwiftNodeOperator<T extends SwiftNode> {

    T operate(T... node);

//    OperatorType getType();

    enum OperatorType {
        // TODO: 2018/6/12 先简单分为两种
        ROW,
        ALL
    }
}

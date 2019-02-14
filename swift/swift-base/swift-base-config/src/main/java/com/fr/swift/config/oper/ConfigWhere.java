package com.fr.swift.config.oper;

/**
 * @author yee
 * @date 2018-12-30
 */
public interface ConfigWhere<T> extends Expression {
    T getValue();

    Type type();

    enum Type {
        //
        EQ, IN, LIKE
    }

    enum MatchMode {
        START, END, ANY
    }
}

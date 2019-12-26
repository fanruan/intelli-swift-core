package com.fr.swift.config.oper;

/**
 * @author Heng.J
 * @date 2019/12/24
 * @description
 * @since swift 1.1
 */
public interface ConfigAggregation extends Expression {

    ConfigAggregation.Type type();

    enum Type {
        MAX, MIN,
    }
}

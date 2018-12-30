package com.fr.swift.config.oper;

import java.util.List;

/**
 * @author yee
 * @date 2018-11-28
 */
public interface ConfigQuery<Entity> {
    List<Entity> executeQuery();

    void where(ConfigWhere... wheres);

    void orderBy(Order... orders);

    int executeUpdate();
}

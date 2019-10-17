package com.fr.swift.config;

import com.fr.swift.config.command.SwiftConfigCommandBus;
import com.fr.swift.config.query.SwiftConfigQueryBus;

/**
 * @author yee
 * @date 2019-07-30
 */
public interface SwiftConfig {
    /**
     * 获取配置类的操作命令
     *
     * @param tClass 配置类
     * @param <T>
     * @return 操作对象
     */
    <T, Bus extends SwiftConfigCommandBus<T>> Bus command(Class<T> tClass);

    /**
     * 获取通用的CommandBus
     *
     * @return
     */
    SwiftConfigCommandBus command();

    /**
     * 获取配置类的查询
     *
     * @param tClass
     * @param <T>
     * @return
     */
    <T, Bus extends SwiftConfigQueryBus<T>> Bus query(Class<T> tClass);

    /**
     * 获取通用的QueryBus
     *
     * @return
     */
    SwiftConfigQueryBus query();
}

package com.fr.swift.config.impl;

import com.fr.swift.config.SwiftConfig;
import com.fr.swift.config.command.SwiftConfigCommandBus;
import com.fr.swift.config.query.SwiftConfigQueryBus;

/**
 * TODO 实现 注册不同的Entity？还是只用一个
 * @author yee
 * @date 2019-07-30
 */
public class SwiftHibernateConfig implements SwiftConfig {
    @Override
    public <T> SwiftConfigCommandBus<T> command(Class<T> tClass) {
        return null;
    }

    @Override
    public SwiftConfigCommandBus command() {
        return null;
    }

    @Override
    public <T> SwiftConfigQueryBus<T> query(Class<T> tClass) {
        return null;
    }

    @Override
    public SwiftConfigQueryBus query() {
        return null;
    }
}

package com.fr.swift.config.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SwiftConfig;
import com.fr.swift.config.command.SwiftConfigCommandBus;
import com.fr.swift.config.command.impl.SwiftConfigEntityCommandBusImpl;
import com.fr.swift.config.command.impl.SwiftHibernateConfigCommandBus;
import com.fr.swift.config.command.impl.SwiftSegmentCommandBusImpl;
import com.fr.swift.config.entity.SwiftColumnIndexingConf;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.query.SwiftConfigQueryBus;
import com.fr.swift.config.query.impl.SwiftConfigEntityQueryBusImpl;
import com.fr.swift.config.query.impl.SwiftHibernateConfigQueryBus;
import com.fr.swift.config.query.impl.SwiftIndexingConfigQueryBus;
import com.fr.swift.segment.SegmentKey;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yee
 * @date 2019-07-30
 */
@SwiftBean
public class SwiftHibernateConfig implements SwiftConfig {

    private ConcurrentMap<Class<?>, SwiftConfigCommandBus<?>> commandBus = new ConcurrentHashMap<>();
    private ConcurrentMap<Class<?>, SwiftConfigQueryBus<?>> queryBus = new ConcurrentHashMap<>();


    public SwiftHibernateConfig() {
        init();
    }

    private void init() {
        commandBus.putIfAbsent(SwiftConfigEntity.class, new SwiftConfigEntityCommandBusImpl());
        commandBus.putIfAbsent(SegmentKey.class, new SwiftSegmentCommandBusImpl());

        queryBus.putIfAbsent(SwiftColumnIndexingConf.class, new SwiftIndexingConfigQueryBus());
        queryBus.putIfAbsent(SwiftConfigEntity.class, new SwiftConfigEntityQueryBusImpl());
    }

    @Override
    public <T, Bus extends SwiftConfigCommandBus<T>> Bus command(Class<T> tClass) {
        if (!commandBus.containsKey(tClass)) {
            commandBus.put(tClass, new SwiftHibernateConfigCommandBus<T>(tClass));
        }
        return (Bus) commandBus.get(tClass);
    }

    @Override
    public SwiftConfigCommandBus command() {
        return new SwiftHibernateConfigCommandBus(Object.class);
    }

    @Override
    public <T, Bus extends SwiftConfigQueryBus<T>> Bus query(Class<T> tClass) {
        if (!queryBus.containsKey(tClass)) {
            queryBus.put(tClass, new SwiftHibernateConfigQueryBus<T>(tClass));
        }
        return (Bus) queryBus.get(tClass);
    }


    @Override
    public SwiftConfigQueryBus query() {
        return new SwiftHibernateConfigQueryBus(Object.class);
    }
}

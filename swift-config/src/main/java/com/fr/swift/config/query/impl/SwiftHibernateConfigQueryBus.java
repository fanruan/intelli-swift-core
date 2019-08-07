package com.fr.swift.config.query.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigSessionCreator;
import com.fr.swift.config.oper.Page;
import com.fr.swift.util.function.Function;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-30
 */
public class SwiftHibernateConfigQueryBus<T> extends BaseSwiftConfigQueryBus<T> {
    private Class<T> tClass;

    public SwiftHibernateConfigQueryBus(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    protected ConfigSession createSession() throws SQLException {
        try {
            return SwiftContext.get().getBean(ConfigSessionCreator.class).createSession();
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public T select(final Serializable id) {
        return get(SwiftConfigQueries.of(tClass, id));
    }

    @Override
    public <R> R select(Serializable id, Function<T, R> fn) {
        return fn.apply(select(id));
    }

    @Override
    public List<T> get(SwiftConfigCondition condition) {
        return get(SwiftConfigQueries.of(tClass, condition));
    }

    @Override
    public Page<T> page(final int page, final int size, SwiftConfigCondition condition) {
        return get(SwiftConfigQueries.of(tClass, page, size, condition));
    }

    @Override
    public <R> R get(SwiftConfigCondition condition, Function<Collection<T>, R> fn) {
        return fn.apply(get(condition));
    }
}

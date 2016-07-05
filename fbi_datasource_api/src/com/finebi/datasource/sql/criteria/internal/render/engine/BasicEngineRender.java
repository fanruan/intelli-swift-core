package com.finebi.datasource.sql.criteria.internal.render.engine;

import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BasicEngineRender<T> implements RenderExtended<Object> {
    protected Object jpaqlQuery;
    private T delegate;

    public BasicEngineRender(T delegate) {
        this.jpaqlQuery = new StringBuilder();
        this.delegate = delegate;

    }

    public T getDelegate() {
        return delegate;
    }

    @Override
    public Object getRenderResult() {
        return jpaqlQuery;
    }
}
package com.finebi.datasource.sql.criteria.internal.render.engine;

import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BasicEngineRender<T, R> implements RenderExtended<R> {
    private T delegate;
    private boolean negated;

    public BasicEngineRender(T delegate) {
        this.delegate = delegate;

    }

    public T getDelegate() {
        return delegate;
    }

    @Override
    public R getRenderResult() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void negate() {
        negated = true;
    }

    @Override
    public boolean isNegated() {
        return negated;
    }
}
package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.render.LiteralRender;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BasicLiteralRender<T> implements LiteralRender {
    protected StringBuilder jpaqlQuery;
    private T delegate;
    private boolean negated;

    public BasicLiteralRender(T delegate) {
        this.jpaqlQuery = new StringBuilder();
        this.delegate = delegate;

    }

    public T getDelegate() {
        return delegate;
    }

    @Override
    public String getRenderResult() {
        return jpaqlQuery.toString();
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

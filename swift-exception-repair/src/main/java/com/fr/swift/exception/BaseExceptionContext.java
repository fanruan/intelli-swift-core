package com.fr.swift.exception;

import java.util.Objects;

/**
 * @author anchore
 * @date 2019/8/16
 */
abstract class BaseExceptionContext<T> implements ExceptionContext<T> {
    private final T context;

    BaseExceptionContext(T context) {
        this.context = context;
    }

    @Override
    public T getContext() {
        return context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseExceptionContext<?> that = (BaseExceptionContext<?>) o;
        return Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context);
    }
}
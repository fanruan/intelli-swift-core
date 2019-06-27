package com.fr.swift.result.qrs;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SwiftMetaData;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * @author anchore
 */
public class EmptyQueryResultSet<T> implements QueryResultSet<T>, Serializable {
    private static final long serialVersionUID = 2332906185461227299L;

    private static final EmptyQueryResultSet<?> INSTANCE = new EmptyQueryResultSet();

    private EmptyQueryResultSet() {
    }

    public static <T> EmptyQueryResultSet<T> get() {
        return (EmptyQueryResultSet<T>) INSTANCE;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftResultSet convert(SwiftMetaData metaData) {
        return null;
    }

    @Override
    public T getPage() {
        throw new NoSuchElementException();
    }

    @Override
    public boolean hasNextPage() {
        return false;
    }

    @Override
    public void close() {
    }
}

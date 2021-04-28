package com.fr.swift.cloud.query.result.serialize;

import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.io.Serializable;

/**
 * @author lyon
 * @date 2018/12/29
 */
public abstract class BaseSerializedQueryResultSet<T> implements QueryResultSet<T>, Serializable {
    private static final long serialVersionUID = 3284100787389755050L;

    private int fetchSize;
    private T page;
    private boolean hasNextPage;
    private transient SyncInvoker invoker;

    BaseSerializedQueryResultSet(int fetchSize, T page, boolean hasNextPage) {
        this.fetchSize = fetchSize;
        this.page = page;
        this.hasNextPage = hasNextPage;
    }

    public void setInvoker(SyncInvoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public T getPage() {
        T ret = page;
        page = null;
        if (hasNextPage() && invoker != null) {
            BaseSerializedQueryResultSet<T> qrs = invoker.invoke();
            page = qrs.page;
            hasNextPage = qrs.hasNextPage;
        } else {
            hasNextPage = false;
        }
        return ret;
    }

    @Override
    public SwiftResultSet convert(SwiftMetaData metaData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {

    }

    @Override
    public boolean hasNextPage() {
        return page != null || hasNextPage;
    }

    public interface SyncInvoker {

        <D> BaseSerializedQueryResultSet<D> invoke();
    }
}

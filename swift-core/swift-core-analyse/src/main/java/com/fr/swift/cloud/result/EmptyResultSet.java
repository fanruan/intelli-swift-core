package com.fr.swift.cloud.result;

import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.SwiftMetaData;

/**
 * @author yee
 * @date 2018/8/29
 */
public enum EmptyResultSet implements SwiftResultSet {
    //
    INSTANCE;

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return new SwiftMetaDataEntity();
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Row getNextRow() {
        return null;
    }

    @Override
    public void close() {

    }
}

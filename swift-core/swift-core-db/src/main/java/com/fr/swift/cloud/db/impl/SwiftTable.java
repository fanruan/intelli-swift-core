package com.fr.swift.cloud.db.impl;

import com.fr.swift.cloud.db.Table;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.source.core.Core;

/**
 * @author anchore
 * @date 2018/3/28
 */
class SwiftTable implements Table {
    private SourceKey key;

    private SwiftMetaData meta;

    SwiftTable(String tableName, SwiftMetaData meta) {
        this.key = new SourceKey(tableName);
        this.meta = meta;
    }

    SwiftTable(SourceKey key, SwiftMetaData meta) {
        this.key = key;
        this.meta = meta;
    }

    @Override
    public SwiftMetaData getMeta() {
        return getMetadata();
    }

    @Override
    public SourceKey getSourceKey() {
        return key;
    }

    @Override
    public SwiftMetaData getMetadata() {
        return meta;
    }

    @Override
    public Core fetchObjectCore() {
        return null;
    }

    @Override
    public String toString() {
        return getSourceKey().getId();
    }
}
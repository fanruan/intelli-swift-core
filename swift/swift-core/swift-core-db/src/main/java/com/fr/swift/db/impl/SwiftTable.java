package com.fr.swift.db.impl;

import com.fr.swift.db.Table;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.core.Core;

/**
 * @author anchore
 * @date 2018/3/28
 */
class SwiftTable implements Table {
    private SourceKey key;

    private SwiftMetaData meta;

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
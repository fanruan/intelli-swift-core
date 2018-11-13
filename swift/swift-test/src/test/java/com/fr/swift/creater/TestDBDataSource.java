package com.fr.swift.creater;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.core.Core;

/**
 * This class created on 2017-11-30.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class TestDBDataSource implements DataSource {

    private String tableName;

    public TestDBDataSource(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public SourceKey getSourceKey() {
        return new SourceKey(tableName);
    }

    @Override
    public SwiftMetaData getMetadata() {
        return null;
    }

    @Override
    public Core fetchObjectCore() {
        return null;
    }
}

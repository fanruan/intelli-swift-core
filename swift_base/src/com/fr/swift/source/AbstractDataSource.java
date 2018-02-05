package com.fr.swift.source;

import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreGenerator;
import com.fr.swift.util.Util;

/**
 * Created by pony on 2017/11/15.
 */
public abstract class AbstractDataSource implements DataSource {
    protected SourceKey key;
    protected transient SwiftMetaData metaData;
    private transient Core core;

    @Override
    public SourceKey getSourceKey() {
        if (key == null) {
            initSourceKey();
        }
        Util.requireNonNull(key);
        return key;
    }

    protected void initSourceKey() {
        String id = fetchObjectCore().getValue();
        key = new SourceKey(id);
    }

    @Override
    public SwiftMetaData getMetadata() {
        if (metaData == null) {
            initMetaData();
        }
        return metaData;
    }

    protected abstract void initMetaData();

    @Override
    public Core fetchObjectCore() {
        if (core == null) {
            core = new CoreGenerator(this).fetchObjectCore();
        }
        return core;
    }
}

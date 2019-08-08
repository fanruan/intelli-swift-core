package com.fr.swift.config.query.impl;

import com.fr.swift.config.entity.SwiftColumnIndexingConf;
import com.fr.swift.config.entity.key.ColumnId;
import com.fr.swift.source.SourceKey;

/**
 * @author yee
 * @date 2019-08-07
 */
public class SwiftIndexingConfigQueryBus extends SwiftHibernateConfigQueryBus<SwiftColumnIndexingConf> {
    public SwiftIndexingConfigQueryBus() {
        super(SwiftColumnIndexingConf.class);
    }

    public SwiftColumnIndexingConf getColumnConf(final SourceKey table, final String columnName) {
        ColumnId id = new ColumnId(table, columnName);
        final SwiftColumnIndexingConf select = select(id);
        return null == select ? new SwiftColumnIndexingConf(table, columnName, true, false) : select;
    }
}

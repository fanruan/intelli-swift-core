package com.fr.swift.generate.conf.service;

import com.fr.swift.generate.conf.ColumnIndexingConf;
import com.fr.swift.generate.conf.SwiftColumnIndexingConf;
import com.fr.swift.generate.conf.SwiftTableIndexingConf;
import com.fr.swift.generate.conf.TableIndexingConf;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.line.LineAllotRule;

/**
 * @author anchore
 * @date 2018/7/2
 */
public class SwiftIndexingConfService implements IndexingConfService {
    @Override
    public TableIndexingConf getTableConf(SourceKey table) {
        return new SwiftTableIndexingConf(table, new LineAllotRule());
    }

    @Override
    public ColumnIndexingConf getColumnConf(SourceKey table, String columnName) {
        return new SwiftColumnIndexingConf(table, columnName, true, true);
    }

    private static final IndexingConfService INSTANCE = new SwiftIndexingConfService();

    private SwiftIndexingConfService() {
    }

    public static IndexingConfService get() {
        return INSTANCE;
    }
}
package com.fr.swift.config.service;

import com.fr.swift.config.entity.SwiftColumnIndexingConf;
import com.fr.swift.config.entity.SwiftTableAllotConf;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/7/2
 */
public interface IndexingConfService {
    SwiftTableAllotConf getTableConf(SourceKey table);

    SwiftColumnIndexingConf getColumnConf(SourceKey table, String columnName);

    void setTableConf(SwiftTableAllotConf conf);

    void setColumnConf(SwiftColumnIndexingConf conf);
}
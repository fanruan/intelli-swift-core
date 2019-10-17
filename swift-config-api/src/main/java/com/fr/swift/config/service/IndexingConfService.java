package com.fr.swift.config.service;

import com.fr.swift.config.entity.SwiftColumnIndexingConf;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/7/2
 */
public interface IndexingConfService {

    SwiftColumnIndexingConf getColumnConf(SourceKey table, String columnName);

    void setColumnConf(SwiftColumnIndexingConf conf);
}
package com.fr.swift.generate.conf.service;

import com.fr.swift.generate.conf.ColumnIndexingConf;
import com.fr.swift.generate.conf.TableIndexingConf;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/7/2
 */
public interface IndexingConfService {
    TableIndexingConf getTableConf(SourceKey table);

    ColumnIndexingConf getColumnConf(SourceKey table, String columnName);
}
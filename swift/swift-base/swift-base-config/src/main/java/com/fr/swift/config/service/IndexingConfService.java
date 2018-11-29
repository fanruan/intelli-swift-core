package com.fr.swift.config.service;

import com.fr.swift.config.ColumnIndexingConf;
import com.fr.swift.config.TableIndexingConf;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/7/2
 */
public interface IndexingConfService {
    TableIndexingConf getTableConf(SourceKey table);

    ColumnIndexingConf getColumnConf(SourceKey table, String columnName);

    void setTableConf(TableIndexingConf conf);

    void setColumnConf(ColumnIndexingConf conf);
}
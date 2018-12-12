package com.fr.swift.config.service;

import com.fr.swift.config.ColumnIndexingConf;
import com.fr.swift.config.TableAllotConf;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/7/2
 */
public interface IndexingConfService {
    TableAllotConf getTableConf(SourceKey table);

    ColumnIndexingConf getColumnConf(SourceKey table, String columnName);

    void setTableConf(TableAllotConf conf);

    void setColumnConf(ColumnIndexingConf conf);
}
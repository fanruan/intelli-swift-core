package com.fr.swift.config.service;

import com.fr.swift.config.bean.SwiftColumnIdxConfBean;
import com.fr.swift.config.bean.SwiftTableAllotConfBean;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/7/2
 */
public interface IndexingConfService {
    SwiftTableAllotConfBean getTableConf(SourceKey table);

    SwiftColumnIdxConfBean getColumnConf(SourceKey table, String columnName);

    void setTableConf(SwiftTableAllotConfBean conf);

    void setColumnConf(SwiftColumnIdxConfBean conf);
}
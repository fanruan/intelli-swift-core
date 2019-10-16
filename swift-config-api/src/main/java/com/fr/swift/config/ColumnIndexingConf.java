package com.fr.swift.config;

import com.fr.swift.config.service.IndexingConf;

/**
 * @author anchore
 * @date 2018/7/2
 */
public interface ColumnIndexingConf extends IndexingConf {
    String getColumn();

    boolean isRequireIndex();

    boolean isRequireGlobalDict();
}
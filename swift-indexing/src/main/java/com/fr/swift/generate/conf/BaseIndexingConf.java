package com.fr.swift.generate.conf;

import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/7/2
 */
abstract class BaseIndexingConf implements IndexingConf {
    SourceKey table;

    BaseIndexingConf(SourceKey table) {
        this.table = table;
    }
}
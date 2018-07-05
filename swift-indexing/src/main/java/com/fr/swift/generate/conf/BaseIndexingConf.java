package com.fr.swift.generate.conf;

import com.fr.swift.source.SourceKey;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Id;
import com.fr.third.javax.persistence.MappedSuperclass;

/**
 * @author anchore
 * @date 2018/7/2
 */
@MappedSuperclass
abstract class BaseIndexingConf implements IndexingConf {
    @Id
    @Column(name = "tableKey")
    String tableKey;

    BaseIndexingConf(SourceKey tableKey) {
        this.tableKey = tableKey.getId();
    }
}
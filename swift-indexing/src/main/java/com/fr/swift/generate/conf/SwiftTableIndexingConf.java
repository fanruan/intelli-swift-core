package com.fr.swift.generate.conf;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Table;

/**
 * @author anchore
 * @date 2018/7/2
 */
@Entity
@Table(name = "fine_swift_table_index_conf")
public class SwiftTableIndexingConf extends BaseIndexingConf implements TableIndexingConf {
    private AllotRule allotRule;

    public SwiftTableIndexingConf(SourceKey table, AllotRule allotRule) {
        super(table);
        this.allotRule = allotRule;
    }

    @Override
    public AllotRule getAllotRule() {
        return allotRule;
    }

    @Override
    public SourceKey getTable() {
        return new SourceKey(tableKey);
    }
}
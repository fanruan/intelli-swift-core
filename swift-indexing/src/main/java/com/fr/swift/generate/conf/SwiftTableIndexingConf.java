package com.fr.swift.generate.conf;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.AllotRule;

/**
 * @author anchore
 * @date 2018/7/2
 */
public class SwiftTableIndexingConf extends BaseIndexingConf implements TableIndexingConf {
    private AllotRule allotRule;

    public SwiftTableIndexingConf(TableIndexingConf conf) {
        this(conf.getTable(), conf.getAllotRule());
    }

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
        return table;
    }
}
package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.BaseHistorySourceAlloter;

/**
 * @author anchore
 * @date 2018/12/19
 */
public class HistoryHashSourceAlloter extends BaseHistorySourceAlloter<HashAllotRule, HashRowInfo> {

    public HistoryHashSourceAlloter(SourceKey tableKey, HashAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected int getLogicOrder(HashRowInfo rowInfo) {
        return rule.analyzer().analyseHistory(rowInfo);
    }
}
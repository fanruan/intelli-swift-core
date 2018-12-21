package com.fr.swift.source.alloter.impl.line;


import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.BaseHistorySourceAlloter;

/**
 * @author yee
 * @date 2017/12/13
 */
public class HistoryLineSourceAlloter extends BaseHistorySourceAlloter<LineAllotRule, LineRowInfo> {

    public HistoryLineSourceAlloter(SourceKey tableKey, LineAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected int getLogicOrder(LineRowInfo rowInfo) {
        return (int) (rowInfo.getCursor() / rule.getCapacity());
    }
}
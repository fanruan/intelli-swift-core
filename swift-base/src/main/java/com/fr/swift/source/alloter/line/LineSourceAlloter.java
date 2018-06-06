package com.fr.swift.source.alloter.line;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.BaseSourceAlloter;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;

/**
 * @author yee
 * @date 2017/12/13
 */
public class LineSourceAlloter extends BaseSourceAlloter {
    private LineAllotRule rule;

    public LineSourceAlloter(SourceKey tableKey) {
        this(tableKey, new LineAllotRule());
    }

    public LineSourceAlloter(SourceKey tableKey, LineAllotRule rule) {
        super(tableKey);
        this.rule = rule;
    }

    @Override
    public SegmentInfo allot(RowInfo rowInfo) {
        int order = (int) (rowInfo.getCursor() / rule.getStep());
        return new SwiftSegmentInfo(order);
    }

    @Override
    public AllotRule getAllotRule() {
        return rule;
    }
}

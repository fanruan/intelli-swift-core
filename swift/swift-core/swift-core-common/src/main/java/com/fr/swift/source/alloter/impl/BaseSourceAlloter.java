package com.fr.swift.source.alloter.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

/**
 * @author anchore
 * @date 2018/12/21
 */
public abstract class BaseSourceAlloter<A extends AllotRule, R extends RowInfo> implements SwiftSourceAlloter<A, R> {

    static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    SourceKey tableKey;

    protected A rule;

    protected BaseSourceAlloter(SourceKey tableKey, A rule) {
        this.tableKey = tableKey;
        this.rule = rule;
    }

    @Override
    public A getAllotRule() {
        return rule;
    }

    static class SegmentState {

        private SegmentInfo segInfo;

        int cursor = -1;

        SegmentState(SegmentInfo segInfo) {
            this.segInfo = segInfo;
        }

        int incrementAndGet() {
            return ++cursor;
        }

        SegmentInfo getSegInfo() {
            return segInfo;
        }
    }
}
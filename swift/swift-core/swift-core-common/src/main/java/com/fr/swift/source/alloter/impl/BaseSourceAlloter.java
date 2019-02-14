package com.fr.swift.source.alloter.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/12/21
 */
public abstract class BaseSourceAlloter<A extends AllotRule, R extends RowInfo> implements SwiftSourceAlloter<A, R> {

    protected static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    protected SourceKey tableKey;

    protected A rule;

    /**
     * 逻辑order到实际order的映射
     * 处理order竞争的
     */
    private Map<Integer, SegmentState> logicToReal = new HashMap<Integer, SegmentState>();

    protected BaseSourceAlloter(SourceKey tableKey, A rule) {
        this.tableKey = tableKey;
        this.rule = rule;
    }

    @Override
    public SegmentInfo allot(R rowInfo) {
        int logicOrder = getLogicOrder(rowInfo);

        SegmentState segState;
        if (logicToReal.containsKey(logicOrder)) {
            // 已分配
            segState = logicToReal.get(logicOrder);
        } else {
            // 新分配
            segState = getInsertableSeg();
            logicToReal.put(logicOrder, segState);
        }
        if (segState.incrementAndGet() < rule.getCapacity()) {
            // 未满
            return segState.getSegInfo();
        }
        // 已满，再分配
        segState = getInsertableSeg();
        logicToReal.put(logicOrder, segState);
        segState.incrementAndGet();
        return segState.getSegInfo();
    }

    protected abstract SegmentState getInsertableSeg();

    /**
     * 计算逻辑seg order
     *
     * @param rowInfo 行
     * @return logic order
     */
    protected abstract int getLogicOrder(R rowInfo);

    protected boolean isSegInserting(SegmentInfo segInfo) {
        for (SegmentState segState : logicToReal.values()) {
            if (segInfo.equals(segState.getSegInfo())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public A getAllotRule() {
        return rule;
    }

    protected static class SegmentState {

        private SegmentInfo segInfo;

        private int cursor = -1;

        public SegmentState(SegmentInfo segInfo) {
            this.segInfo = segInfo;
        }

        public SegmentState(SegmentInfo segInfo, int cursor) {
            this.segInfo = segInfo;
            this.cursor = cursor;
        }

        int incrementAndGet() {
            return ++cursor;
        }

        SegmentInfo getSegInfo() {
            return segInfo;
        }
    }
}
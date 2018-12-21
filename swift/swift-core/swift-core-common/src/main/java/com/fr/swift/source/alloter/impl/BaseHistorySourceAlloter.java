package com.fr.swift.source.alloter.impl;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/6/5
 */
public abstract class BaseHistorySourceAlloter<A extends AllotRule, R extends RowInfo> extends BaseSourceAlloter<A, R> {

    /**
     * 逻辑order到实际order的映射
     * 处理order竞争的
     */
    private Map<Integer, SegmentState> logicToReal = new HashMap<Integer, SegmentState>();

    public BaseHistorySourceAlloter(SourceKey tableKey, A rule) {
        super(tableKey, rule);
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
            segState = append(logicOrder);
        }
        if (segState.incrementAndGet() < rule.getCapacity()) {
            // 未满
            return segState.getSegInfo();
        }
        // 已满，再分配
        return append(logicOrder).getSegInfo();
    }

    private SegmentState append(int logicOrder) {
        // todo hash出的seg key可能还要写入此seg key的hash值
        // todo 另外还要处理脏配置
        SegmentKey segKey = SEG_SVC.tryAppendSegment(tableKey, StoreType.FINE_IO);
        SwiftSegmentInfo segInfo = new SwiftSegmentInfo(segKey.getOrder(), StoreType.FINE_IO);
        SegmentState segState = new SegmentState(segInfo);
        logicToReal.put(logicOrder, segState);
        return segState;
    }

    /**
     * 计算逻辑seg order
     *
     * @param rowInfo 行
     * @return logic order
     */
    protected abstract int getLogicOrder(R rowInfo);
}
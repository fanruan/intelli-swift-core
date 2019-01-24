package com.fr.swift.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.CommonProcessHandler;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/5
 */
public interface HistoryService extends SwiftService {
    /**
     * truncate
     *
     * @param tableKey table key
     */
    @InvokeMethod(value = CommonProcessHandler.class, target = Target.HISTORY)
    void truncate(SourceKey tableKey);

    @InvokeMethod(value = CommonProcessHandler.class, target = Target.HISTORY)
    void removeHistory(List<SegmentKey> needRemoveList);
}

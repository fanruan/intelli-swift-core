package com.fr.swift.service;

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
    void truncate(SourceKey tableKey);

    void removeHistory(List<SegmentKey> needRemoveList);
}

package com.fr.swift.cloud.service;

import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.source.SourceKey;

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

package com.fr.swift;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.SwiftService;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * This class created on 2018/7/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface CollateService extends SwiftService {

    /**
     * 自动整理合并所有增量块
     *
     * @param tableKey
     * @throws Exception
     */
    void autoCollateRealtime(SourceKey tableKey) throws Exception;

    /**
     * 自动整理合并所有历史块
     *
     * @param tableKey
     * @throws Exception
     */
    void autoCollateHistory(SourceKey tableKey) throws Exception;

    /**
     * 指定整理合并增量块
     *
     * @param segmentKeyList
     * @throws Exception
     */
    void appointCollateRealtime(List<SegmentKey> segmentKeyList) throws Exception;

    /**
     * 指定整理合并历史块
     *
     * @param segmentKeyList
     * @throws Exception
     */
    void appointCollateHistory(List<SegmentKey> segmentKeyList) throws Exception;

    void persistRealtime(SourceKey tableKey, Segment segment);
}

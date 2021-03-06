package com.fr.swift.cloud.service;

import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.source.SourceKey;

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
     * 指定整理合并块
     *
     * @param tableKey
     * @param segmentKeyList
     * @throws Exception
     */
    List<SegmentKey> appointCollate(SourceKey tableKey, List<SegmentKey> segmentKeyList) throws Exception;

}
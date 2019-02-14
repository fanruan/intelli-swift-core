package com.fr.swift.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.handler.CollateProcessHandler;
import com.fr.swift.segment.SegmentKey;
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
     * 指定整理合并块
     *
     * @param tableKey
     * @param segmentKeyList
     * @throws Exception
     */
    @InvokeMethod(value = CollateProcessHandler.class)
    void appointCollate(SourceKey tableKey, List<SegmentKey> segmentKeyList) throws Exception;

    /**
     * 自动整理合并块
     *
     * @param tableKey
     * @throws Exception
     */
    void autoCollate(SourceKey tableKey) throws Exception;
}
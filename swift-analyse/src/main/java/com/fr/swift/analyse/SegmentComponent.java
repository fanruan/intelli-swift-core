package com.fr.swift.analyse;

import com.fr.swift.segment.Segment;

import java.util.List;

/**
 * @author xiqiu
 * @date 2020/12/18
 * @description 流式查询时的块容器，在被调用的时候提供下一批次用于查询的块
 * @since swift-1.2.0
 */
public interface SegmentComponent {

    /**
     * 返回需要的块，不需要参数
     *
     * @return 返回需要的块
     */
    List<Segment> getNextBatchSegments();

    /**
     * 是否还有块了
     *
     * @return 是否还有更多的块
     */
    boolean isEmpty();

}

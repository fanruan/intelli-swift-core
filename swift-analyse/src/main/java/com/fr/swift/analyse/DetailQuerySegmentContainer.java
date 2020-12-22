package com.fr.swift.analyse;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiqiu
 * @date 2020/12/18
 * @description 不排序明细流式查询的块提供与存储者
 * @since swift-1.2.0
 */
public class DetailQuerySegmentContainer implements SegmentContainer {
    private static final int BATCH_SIZE = 1;
    private List<Segment> segments;
    private int index = 0;

    public DetailQuerySegmentContainer(List<Segment> segments) {
        this.segments = segments;
    }

    /**
     * 不排序明细查询的块获取函数，暂定只返回一块，可以考虑一次返回多块
     *
     * @return 返回下一个批次的块
     */
    @Override
    public List<Segment> getNextBatchSegments() {
        List<Segment> result = new ArrayList<>();
        int count = 0;
        while (index < segments.size() && count < BATCH_SIZE) {
            result.add(segments.get(index++));
            count++;
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return index >= segments.size();
    }

    @Override
    public void releaseSegments() {
        SegmentUtils.releaseHisSeg(segments);
    }
}

package com.fr.swift.analyse;

import com.fr.swift.segment.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author xiqiu
 * @date 2020/12/18
 * @description 不排序明细流式查询的块提供与存储者
 * @since swift-1.2.0
 */
public class DetailQuerySegmentComponent implements SegmentComponent {
    private static final int BATCH_SIZE = 2;
    private Stack<Segment> segments = new Stack<>();

    public DetailQuerySegmentComponent(List<Segment> segments) {
        this.segments.addAll(segments);
    }

    /**
     * 不排序明细查询的块获取函数，暂定只返回一块，可以考虑一次返回多块
     * 获取方需要注意释放资源
     * @return 返回下一个批次的块
     */
    @Override
    public List<Segment> getNextBatchSegments() {
        List<Segment> result = new ArrayList<>();
        int count = 0;
        while (!segments.isEmpty() && count < BATCH_SIZE) {
            result.add(segments.pop());
            count++;
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return segments.isEmpty();
    }
}

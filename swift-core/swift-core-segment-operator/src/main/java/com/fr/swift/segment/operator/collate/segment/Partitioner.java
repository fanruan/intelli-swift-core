package com.fr.swift.segment.operator.collate.segment;

import com.fr.swift.segment.Segment;

import java.util.List;

/**
 * Created by lyon on 2019/2/21.
 */
public interface Partitioner {

    /**
     * 按照一定规则，对待合并的碎片块进行划分，每个SegmentItem包含生成一个新块所需碎片块
     *
     * @param segments 待合并的碎片块
     * @return
     */
    List<SegmentItem> partition(List<Segment> segments);
}

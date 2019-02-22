package com.fr.swift.segment.operator.collate.segment;

import com.fr.swift.segment.Segment;

import java.util.List;

/**
 * Created by lyon on 2019/2/21.
 */
public interface Partitioner {

    List<SegmentItem> partition(List<Segment> segments);
}

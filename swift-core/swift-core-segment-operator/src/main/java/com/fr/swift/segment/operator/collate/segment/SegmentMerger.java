package com.fr.swift.segment.operator.collate.segment;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

import java.util.List;

/**
 * Created by lyon on 2019/2/20.
 */
public interface SegmentMerger {

    List<SegmentKey> merge(DataSource dataSource, List<Segment> segments, SwiftSourceAlloter alloter) throws Exception;
}

package com.fr.swift.segment.operator.collate.segment;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

import java.util.List;

/**
 * Created by lyon on 2019/2/20.
 */
public interface HisSegmentMerger {

    /**
     * 合并多个历史块碎片到新的历史块
     *
     * @param dataSource 表
     * @param segments   要合并的块
     * @param alloter    分块规则
     * @return 新块的SegmentKey
     * @throws Exception
     */
    List<SegmentKey> merge(DataSource dataSource, List<Segment> segments, SwiftSourceAlloter alloter, int index);
}

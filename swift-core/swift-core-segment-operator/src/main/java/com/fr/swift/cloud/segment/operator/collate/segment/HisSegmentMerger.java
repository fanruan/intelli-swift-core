package com.fr.swift.cloud.segment.operator.collate.segment;

import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.source.DataSource;

import java.util.List;

/**
 * Created by lyon on 2019/2/20.
 */
public interface HisSegmentMerger {

    /**
     * 合并多个历史块碎片到新的历史块
     *
     * @param dataSource        表
     * @param segmentPartitions 要合并的块
     * @return 新块的SegmentKey
     * @throws Exception
     */
    List<SegmentKey> merge(DataSource dataSource, List<SegmentPartition> segmentPartitions, int index);
}

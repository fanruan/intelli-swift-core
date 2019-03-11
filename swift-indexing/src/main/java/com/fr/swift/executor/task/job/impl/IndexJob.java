package com.fr.swift.executor.task.job.impl;

import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;

import java.util.Collections;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description 索引先放在transfer里一起，暂时弃用
 */
public class IndexJob extends BaseJob<Boolean, SegmentKey> {

    private SegmentKey indexSegKey;

    public IndexJob(SegmentKey indexSegKey) {
        this.indexSegKey = indexSegKey;
    }

    @Override
    public Boolean call() {
        try {
            Segment indexSegment = SegmentUtils.newSegment(indexSegKey);
            SegmentUtils.indexSegmentIfNeed(Collections.singletonList(indexSegment));
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }
}

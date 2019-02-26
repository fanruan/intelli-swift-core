package com.fr.swift.executor.task.job.impl;

import com.fr.swift.executor.task.job.Job;
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
@Deprecated
public class IndexJob implements Job<Boolean> {

    private SegmentKey indexSegkey;

    public IndexJob(SegmentKey indexSegkey) {
        this.indexSegkey = indexSegkey;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            Segment indexSegment = SegmentUtils.newSegment(indexSegkey);
            SegmentUtils.indexSegmentIfNeed(Collections.singletonList(indexSegment));
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }
}

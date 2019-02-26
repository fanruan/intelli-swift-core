package com.fr.swift.executor.task.job.impl;

import com.fr.swift.executor.task.job.Job;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class UploadJob implements Job<Boolean> {

    private SegmentKey uploadSegmentKey;

    public UploadJob(SegmentKey uploadSegmentKey) {
        this.uploadSegmentKey = uploadSegmentKey;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            // TODO: 2019/2/19 upload
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }
}

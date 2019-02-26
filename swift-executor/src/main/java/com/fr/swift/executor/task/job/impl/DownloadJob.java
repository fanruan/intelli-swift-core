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
public class DownloadJob implements Job<Boolean> {

    private SegmentKey downloadSegmentKey;

    public DownloadJob(SegmentKey downloadSegmentKey) {
        this.downloadSegmentKey = downloadSegmentKey;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            // TODO: 2019/2/19 download
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }
}

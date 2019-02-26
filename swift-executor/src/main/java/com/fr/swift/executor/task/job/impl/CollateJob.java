package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.CollateService;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class CollateJob implements Job<Boolean> {

    private SourceKey tableKey;
    private List<SegmentKey> segmentKeyList;

    public CollateJob(SourceKey tableKey, List<SegmentKey> segmentKeyList) {
        this.tableKey = tableKey;
        this.segmentKeyList = segmentKeyList;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            CollateService collateService = SwiftContext.get().getBean(CollateService.class);
            collateService.appointCollate(tableKey, segmentKeyList);
            return false;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return true;
        }
    }
}

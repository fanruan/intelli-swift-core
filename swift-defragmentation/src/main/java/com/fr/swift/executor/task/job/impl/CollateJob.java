package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.service.CollateService;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class CollateJob extends BaseJob<List<SegmentKey>, List<String>> {

    private SourceKey tableKey;

    private List<String> segmentIds;

    private SegmentService segmentService;

    public CollateJob(SourceKey tableKey, List<String> segmentIds) {
        this.tableKey = tableKey;
        this.segmentIds = segmentIds;
        this.segmentService = SwiftContext.get().getBean(SegmentService.class);
    }

    @Override
    public List<SegmentKey> call() throws Exception {
        CollateService collateService = SwiftContext.get().getBean(CollateService.class);
        return collateService.appointCollate(tableKey, segmentService.getSegmentKeysByIds(tableKey, segmentIds));
    }

    @Override
    public List<String> serializedTag() {
        return segmentIds;
    }
}

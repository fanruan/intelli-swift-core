package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.service.CollateService;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class CollateJob extends BaseJob<Boolean, List<String>> {

    private SourceKey tableKey;

    private List<String> segmentIds;

    private SwiftSegmentManager segmentManager;

    public CollateJob(SourceKey tableKey, List<String> segmentIds) {
        this.tableKey = tableKey;
        this.segmentIds = segmentIds;
        this.segmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
    }

    @Override
    public Boolean call() {
        try {
            CollateService collateService = SwiftContext.get().getBean(CollateService.class);
            collateService.appointCollate(tableKey, segmentManager.getSegmentKeysByIds(tableKey, segmentIds));
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }

    @Override
    public List<String> serializedTag() {
        return segmentIds;
    }
}

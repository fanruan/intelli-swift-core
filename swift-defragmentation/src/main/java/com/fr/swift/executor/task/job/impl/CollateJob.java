package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.CollateService;
import com.fr.swift.source.SourceKey;

import java.io.Serializable;
import java.util.List;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class CollateJob extends BaseJob<Boolean, List<SegmentKey>> implements Serializable {

    private static final long serialVersionUID = -8915567088027007394L;

    private SourceKey tableKey;

    private List<SegmentKey> segmentKeyList;

    public CollateJob(SourceKey tableKey, List<SegmentKey> segmentKeyList) {
        this.tableKey = tableKey;
        this.segmentKeyList = segmentKeyList;
    }

    @Override
    public Boolean call() {
        try {
            CollateService collateService = SwiftContext.get().getBean(CollateService.class);
            collateService.appointCollate(tableKey, segmentKeyList);
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }

    @Override
    public List<SegmentKey> serializedTag() {
        return segmentKeyList;
    }
}

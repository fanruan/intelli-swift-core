package com.fr.swift.cloud.executor.task.job.impl;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.executor.task.bean.CollateBean;
import com.fr.swift.cloud.executor.task.job.BaseJob;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.service.CollateService;
import com.fr.swift.cloud.source.SourceKey;

import java.util.List;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class CollateJob extends BaseJob<List<SegmentKey>, CollateBean> {

    private SourceKey tableKey;

    private List<String> segmentIds;

    private SegmentService segmentService;

    private CollateBean collateBean;

    public CollateJob(CollateBean collateBean) {
        this.collateBean = collateBean;
        this.tableKey = collateBean.getSourceKey();
        this.segmentIds = collateBean.getSegmentIds();
        this.segmentService = SwiftContext.get().getBean(SegmentService.class);
    }

    @Override
    public List<SegmentKey> call() throws Exception {
        CollateService collateService = SwiftContext.get().getBean(CollateService.class);
        return collateService.appointCollate(tableKey, segmentService.getSegmentKeysByIds(tableKey, segmentIds));
    }

    @Override
    public CollateBean serializedTag() {
        return collateBean;
    }
}

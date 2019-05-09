package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.UploadService;

import java.util.Collections;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class UploadJob extends BaseJob<Boolean, UploadJob> {

    private SegmentKey uploadSegmentKey;

    private boolean uploadWholeSeg;

    public UploadJob(SegmentKey uploadSegmentKey, boolean uploadWholeSeg, JobListener jobListener) {
        super(jobListener);
        this.uploadSegmentKey = uploadSegmentKey;
        this.uploadWholeSeg = uploadWholeSeg;
    }

    @Override
    public Boolean call() {
        try {
            UploadService uploadService = SwiftContext.get().getBean(UploadService.class);
            if (uploadWholeSeg) {
                uploadService.upload(Collections.singleton(uploadSegmentKey));
            } else {
                uploadService.uploadAllShow(Collections.singleton(uploadSegmentKey));
            }
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }

    @Override
    public UploadJob serializedTag() {
        return this;
    }
}
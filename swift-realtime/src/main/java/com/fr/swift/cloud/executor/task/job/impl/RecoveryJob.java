package com.fr.swift.cloud.executor.task.job.impl;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.executor.task.job.BaseJob;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.recover.SegmentRecovery;

import java.util.Collections;

/**
 * @author anchore
 * @date 2019/2/26
 */
public class RecoveryJob extends BaseJob<Void, SegmentKey> {

    private SegmentKey recoverSegKey;

    public RecoveryJob(SegmentKey recoverSegKey) {
        this.recoverSegKey = recoverSegKey;
    }

    @Override
    public Void call() {
        SegmentRecovery segmentRecovery = SwiftContext.get().getBean(SegmentRecovery.class);
        segmentRecovery.recover(Collections.singletonList(recoverSegKey));
        return null;
    }

    @Override
    public SegmentKey serializedTag() {
        return recoverSegKey;
    }
}
package com.fr.swift.cloud.segment.recover;

import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.source.SourceKey;

import java.util.List;

/**
 * @author anchore
 * @date 2018/5/23
 */
public interface SegmentRecovery {
    void recover(List<SegmentKey> segmentKeys);

    void recover(SourceKey tableKey);

    void recoverAll();
}
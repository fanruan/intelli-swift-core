package com.fr.swift.segment.recover;

import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

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
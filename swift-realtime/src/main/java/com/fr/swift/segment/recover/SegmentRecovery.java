package com.fr.swift.segment.recover;

import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/5/23
 */
public interface SegmentRecovery {
    void recover(SourceKey tableKey);

    void recoverAll();
}
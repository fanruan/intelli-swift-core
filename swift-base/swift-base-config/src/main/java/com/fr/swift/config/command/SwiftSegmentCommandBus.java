package com.fr.swift.config.command;

import com.fr.swift.segment.SegmentKey;

import java.util.List;

/**
 * @author yee
 * @date 2019-08-09
 */
public interface SwiftSegmentCommandBus extends SwiftConfigCommandBus<SegmentKey> {
    boolean removeSegments(List<SegmentKey> segmentKeys);
}

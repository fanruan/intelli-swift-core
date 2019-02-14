package com.fr.swift.segment.collate;

import com.fr.swift.segment.SegmentKey;

import java.util.List;

/**
 * @author anchore
 * @date 2018/7/27
 */
public interface FragmentCollectRule {
    List<SegmentKey> collect(List<SegmentKey> segKeys);
}
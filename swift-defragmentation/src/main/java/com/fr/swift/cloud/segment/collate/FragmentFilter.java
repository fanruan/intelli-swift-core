package com.fr.swift.cloud.segment.collate;

import com.fr.swift.cloud.segment.SegmentKey;

import java.util.Collection;
import java.util.List;

/**
 * @author anchore
 * @date 2018/7/27
 */
public interface FragmentFilter {
    List<SegmentKey> filter(Collection<SegmentKey> segKeys);
}
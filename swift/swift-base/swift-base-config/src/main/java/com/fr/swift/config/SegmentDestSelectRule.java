package com.fr.swift.config;

import com.fr.swift.segment.SegmentDestination;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/19
 */
public interface SegmentDestSelectRule {
    List<SegmentDestination> selectDestination(List<SegmentDestination> duplicate);
}

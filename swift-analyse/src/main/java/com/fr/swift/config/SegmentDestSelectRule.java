package com.fr.swift.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fr.swift.segment.SegmentDestination;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/19
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface SegmentDestSelectRule {
    List<SegmentDestination> selectDestination(List<SegmentDestination> duplicate);
}

package com.fr.swift.cloud.segment.operator;

import com.fr.swift.cloud.segment.Segment;

import java.util.List;

/**
 * This class created on 2018/3/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface Merger {
    List<Segment> merge() throws Exception;
}

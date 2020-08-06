package com.fr.swift.segment;

import java.util.Date;

/**
 * @author Moira
 * @date 2020/8/5
 * @description
 * @since swift-1.2.0
 */
public interface SegmentVisited {
    int getVisits();

    String getId();

    Date getVisitedTime();
}

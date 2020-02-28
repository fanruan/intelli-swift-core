package com.fr.swift.exception;

import com.fr.swift.segment.SegmentKey;

/**
 * @author anchore
 * @date 2019/8/16
 */
public class SegmentExceptionContext implements ExceptionContext {
    private SegmentKey segmentKey;

    public SegmentExceptionContext(SegmentKey context) {
        this.segmentKey = context;
    }

    public SegmentKey getSegmentKey() {
        return segmentKey;
    }
}
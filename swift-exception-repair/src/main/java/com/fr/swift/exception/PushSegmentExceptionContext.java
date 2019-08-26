package com.fr.swift.exception;

import com.fr.swift.segment.SegmentLocationInfo;

/**
 * @author anner
 * @this class created on date 2019/8/22
 * @description
 */
public class PushSegmentExceptionContext implements ExceptionContext {
    private SegmentLocationInfo info;

    public PushSegmentExceptionContext(SegmentLocationInfo info) {
        this.info = info;
    }

    public SegmentLocationInfo getInfo() {
        return info;
    }
}

package com.fr.swift.exception;

import com.fr.swift.segment.SegmentKey;

/**
 * @author anchore
 * @date 2019/8/16
 */
public class SegmentExceptionContext extends BaseExceptionContext<SegmentKey> {
    public SegmentExceptionContext(SegmentKey context) {
        super(context);
    }
}
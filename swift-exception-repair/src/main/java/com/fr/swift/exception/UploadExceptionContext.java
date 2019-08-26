package com.fr.swift.exception;

import com.fr.swift.segment.SegmentKey;

/**
 * @author Marvin
 * @date 8/22/2019
 * @description
 * @since swift 1.1
 */
public class UploadExceptionContext implements ExceptionContext {
    private SegmentKey context;
    private boolean isAllShow;

    public UploadExceptionContext(SegmentKey context, boolean isAllShow) {
        this.context = context;
        this.isAllShow = isAllShow;
    }

    public SegmentKey getSegmentKey() {
        return context;
    }

    public boolean isAllShow() {
        return isAllShow;
    }
}

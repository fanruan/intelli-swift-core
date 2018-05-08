package com.fr.swift.source.alloter;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftSourceAlloter;
import com.fr.swift.util.Crasher;

/**
 * @author yee
 * @date 2017/12/13
 */
public class LineSourceAlloter implements SwiftSourceAlloter {

    private static final int DEFAULT_STEP = 10000000;

    private int allotStep;

    public LineSourceAlloter(int num) {
        this.allotStep = num;
    }

    public LineSourceAlloter(SourceKey sourceKey) {
        this(DEFAULT_STEP);
    }

    @Override
    public int allot(long row, String keyColumn, Object data) {
        long segmentIndex = row / allotStep;
        if (segmentIndex > Integer.MAX_VALUE) {
            Crasher.crash("Segment count cannot more than " + Integer.MAX_VALUE);
        }
        return (int) segmentIndex;
    }

    @Override
    public int getAllotStep() {
        return allotStep;
    }
}

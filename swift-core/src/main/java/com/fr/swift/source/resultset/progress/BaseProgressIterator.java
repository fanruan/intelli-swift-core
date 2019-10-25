package com.fr.swift.source.resultset.progress;

import com.fr.swift.log.SwiftLoggers;

/**
 * @author anchore
 * @date 2018/7/12
 */
abstract class BaseProgressIterator {
    private static final long ROW_THRESHOLD = 1 << 16, TIME_THRESHOLD = 10000;

    private static final String ELEMENT_UNIT = "elements", SOURCE = "source";

    private long rowThreshold;

    private long timeThreshold;

    private long cursor = 0;

    private long time = System.currentTimeMillis();

    private String source;

    private String elementUnit;

    BaseProgressIterator(String source) {
        this(ROW_THRESHOLD, TIME_THRESHOLD, source, ELEMENT_UNIT);
    }

    public BaseProgressIterator(long rowThreshold, long timeThreshold, String source, String elementUnit) {
        this.rowThreshold = rowThreshold;
        this.timeThreshold = timeThreshold;
        this.source = source;
        this.elementUnit = elementUnit;
    }

    void iterateNext() {
        cursor++;
        if (cursor % rowThreshold == 0) {
            if (System.currentTimeMillis() - time > timeThreshold) {
                SwiftLoggers.getLogger().info("iterated {} {} of {}", cursor, elementUnit, source);
                time = System.currentTimeMillis();
            }
        }
    }

    void iterateOver() {
        SwiftLoggers.getLogger().info("iterate over, iterated {} {} of {}", cursor, elementUnit, source);
    }
}
package com.fr.swift.flow;

/**
 * This class created on 2018-1-15 15:45:14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class TimeControlRule implements FlowControlRule {
    private long startTime;
    private long continuedTime;
    private long currentTime;

    public TimeControlRule(long continuedTime) {
        this.startTime = System.currentTimeMillis();
        this.continuedTime = continuedTime;
    }

    @Override
    public boolean isEnd() {
        currentTime = System.currentTimeMillis();
        return currentTime >= (startTime + continuedTime);
    }

    @Override
    public void reset() {
        startTime = System.currentTimeMillis();
    }
}

package com.fr.swift.flow;

/**
 * This class created on 2018-1-15 15:40:06
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RowNumberControlRule implements FlowControlRule {

    private int maxCount;

    private int currentCount;

    public RowNumberControlRule(int maxCount) {
        this.maxCount = maxCount;
        this.currentCount = 0;
    }

    @Override
    public boolean isEnd() {
        currentCount++;
        return currentCount > maxCount;
    }

    @Override
    public void reset() {
        currentCount = 0;
    }
}

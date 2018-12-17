package com.fr.swift.query.aggregator.funnel.impl.step;

import com.fr.swift.query.aggregator.funnel.IStep;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class NoRepeatedStep implements IStep {

    private int[] eventMap;
    private boolean[][] steps;

    public NoRepeatedStep(int[] eventMap, boolean[][] steps) {
        this.eventMap = eventMap;
        this.steps = steps;
    }

    @Override
    public boolean isEqual(int eventIndex, int event) {
        return steps[eventIndex][event];
    }

    @Override
    public int size() {
        return steps.length;
    }

    @Override
    public boolean isHeadRepeated() {
        return false;
    }

    @Override
    public boolean hasRepeatedEvents() {
        return false;
    }

    @Override
    public IStep toNoRepeatedStep() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getEventIndex(int event) {
        return eventMap[event];
    }
}

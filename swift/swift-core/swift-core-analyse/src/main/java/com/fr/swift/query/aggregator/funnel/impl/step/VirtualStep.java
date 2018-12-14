package com.fr.swift.query.aggregator.funnel.impl.step;

import com.fr.swift.query.aggregator.funnel.IStep;

import java.util.Arrays;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class VirtualStep implements IStep {

    private boolean[][] steps;
    private boolean isHeadRepeated;
    private boolean hasRepeatedEvents;

    public VirtualStep(boolean[][] steps, boolean isHeadRepeated, boolean hasRepeatedEvents) {
        this.steps = steps;
        this.isHeadRepeated = isHeadRepeated;
        this.hasRepeatedEvents = hasRepeatedEvents;
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
        return isHeadRepeated;
    }

    @Override
    public boolean hasRepeatedEvents() {
        return hasRepeatedEvents;
    }

    @Override
    public IStep toNoRepeatedStep() {
        if (hasRepeatedEvents) {
            throw new UnsupportedOperationException();
        }
        int[] eventMap = new int[15];
        Arrays.fill(eventMap, -1);
        for (int i = 0; i < steps.length; i++) {
            for (int j = 0; j < steps[i].length; j++) {
                eventMap[j] = i;
            }
        }
        return new NoRepeatedStep(eventMap, steps);
    }

    @Override
    public int getEventIndex(int event) {
        throw new UnsupportedOperationException();
    }
}

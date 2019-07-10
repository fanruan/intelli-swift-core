package com.fr.swift.query.query.funnel.impl.step;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.query.funnel.IStep;

import java.util.Arrays;
import java.util.List;

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
    private List<ImmutableBitMap> filters;

    public VirtualStep(boolean[][] steps, boolean isHeadRepeated, boolean hasRepeatedEvents, List<ImmutableBitMap> events) {
        this.steps = steps;
        this.isHeadRepeated = isHeadRepeated;
        this.hasRepeatedEvents = hasRepeatedEvents;
        this.filters = events;
    }

    @Override
    public boolean isEqual(int eventIndex, int event, int row) {
        return steps[eventIndex][event] && matches(eventIndex, row);
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
        int[] eventMap = new int[steps[0].length];

        Arrays.fill(eventMap, -1);
        for (int i = 0; i < steps.length; i++) {
            for (int j = 0; j < steps[i].length; j++) {
                if (steps[i][j]) {
                    eventMap[j] = i;
                }
            }
        }
        return new NoRepeatedStep(eventMap, steps, filters);
    }

    @Override
    public int getEventIndex(int event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean matches(int eventIndex, int row) {
        return filters.get(eventIndex).contains(row);
    }
}

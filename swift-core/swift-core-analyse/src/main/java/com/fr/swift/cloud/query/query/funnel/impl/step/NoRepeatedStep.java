package com.fr.swift.cloud.query.query.funnel.impl.step;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.query.query.funnel.IStep;

import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class NoRepeatedStep implements IStep {

    private int[] eventMap;
    private boolean[][] steps;
    private List<ImmutableBitMap> filters;

    public NoRepeatedStep(int[] eventMap, boolean[][] steps, List<ImmutableBitMap> filters) {
        this.eventMap = eventMap;
        this.steps = steps;
        this.filters = filters;
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

    @Override
    public boolean matches(int eventIndex, int row) {
        return -1 != eventIndex && filters.get(eventIndex).contains(row);
    }
}

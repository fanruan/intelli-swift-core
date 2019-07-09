package com.fr.swift.query.funnel.impl.step;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.funnel.IStep;

import java.util.Arrays;
import java.util.Collections;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class Step implements IStep {

    private int[] steps;
    private boolean isHeadRepeated;
    private boolean hasRepeatedEvents;

    public Step(int[] steps, boolean isHeadRepeated, boolean hasRepeatedEvents) {
        this.steps = steps;
        this.isHeadRepeated = isHeadRepeated;
        this.hasRepeatedEvents = hasRepeatedEvents;
    }

    @Override
    public boolean isEqual(int eventIndex, int event, int row) {
        return steps[eventIndex] == event;
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
        int[] eventMap = new int[15];
        boolean[][] flags = new boolean[steps.length][];
        Arrays.fill(eventMap, -1);
        for (int i = 0; i < steps.length; i++) {
            boolean[] flag = new boolean[15];
            flag[steps[i]] = true;
            flags[i] = flag;
            eventMap[steps[i]] = i;
        }
        return new NoRepeatedStep(eventMap, flags, Collections.<ImmutableBitMap>emptyList());
    }

    @Override
    public int getEventIndex(int event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean matches(int eventIndex, int row) {
        return true;
    }
}

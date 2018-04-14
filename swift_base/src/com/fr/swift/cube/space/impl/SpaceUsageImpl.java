package com.fr.swift.cube.space.impl;

import com.fr.swift.cube.space.SpaceUsage;

/**
 * @author anchore
 * @date 2018/4/14
 */
class SpaceUsageImpl implements SpaceUsage {
    private double used, usable, capacity;

    SpaceUsageImpl(double used, double usable, double capacity) {
        this.used = used;
        this.usable = usable;
        this.capacity = capacity;
    }

    @Override
    public double getUsed() {
        return used;
    }

    @Override
    public double getUsable() {
        return usable;
    }

    @Override
    public double getCapacity() {
        return capacity;
    }
}
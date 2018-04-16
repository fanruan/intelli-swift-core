package com.fr.swift.cube.space.impl;

import com.fr.swift.cube.space.SpaceUsage;

/**
 * @author anchore
 * @date 2018/4/14
 */
class SpaceUsageImpl implements SpaceUsage {
    private long used, usable, total;

    SpaceUsageImpl(long used, long usable, long total) {
        this.used = used;
        this.usable = usable;
        this.total = total;
    }

    @Override
    public long getUsed() {
        return used;
    }

    @Override
    public long getUsable() {
        return usable;
    }

    @Override
    public long getTotal() {
        return total;
    }
}
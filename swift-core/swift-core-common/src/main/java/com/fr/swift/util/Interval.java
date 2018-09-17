package com.fr.swift.util;

import com.fr.swift.compare.Comparators;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2018/6/19
 */
public class Interval {
    private Object min, max;
    private boolean minInclusive, maxInclusive;

    public Interval(Object min, Object max, boolean minInclusive, boolean maxInclusive) {
        this.min = min;
        this.max = max;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    public static Interval ofInfiniteMax(Object min, boolean minInclusive) {
        return new Interval(min, Comparators.MAX_INFINITY, minInclusive, false);
    }

    public static Interval ofInfiniteMin(Object max, boolean maxInclusive) {
        return new Interval(Comparators.MIN_INFINITY, max, false, maxInclusive);
    }

    public <V> V getMin() {
        return (V) min;
    }

    public boolean isMinInclusive() {
        return minInclusive;
    }

    public <V> V getMax() {
        return (V) max;
    }

    public boolean isMaxInclusive() {
        return maxInclusive;
    }

    public static boolean isInfiniteMin(Object value) {
        return value == Comparators.MIN_INFINITY;
    }

    public boolean contains(Object v, Comparator<Object> c) {
        if (c.compare(v, min) == 0 && minInclusive) {
            return true;
        }
        if (c.compare(v, min) > 0 && c.compare(v, max) < 0) {
            return true;
        }
        return c.compare(v, max) == 0 && maxInclusive;
    }

    public static boolean isInfiniteMax(Object value) {
        return value == Comparators.MAX_INFINITY;
    }

    public static boolean isInfinite(Object value) {
        return isInfiniteMin(value) || isInfiniteMax(value);
    }
}
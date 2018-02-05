package com.fr.swift.util;

import java.util.Collection;

/**
 * @author anchore
 */
public final class Util {
    public static boolean in(Object check, Object... os) {
        if (check == null) {
            return false;
        }
        for (Object o : os) {
            if (check == o || check.equals(o)) {
                return true;
            }
        }
        return false;
    }

    public static void requireNonNull(Object o, String err) {
        if (o == null) {
            throw new NullPointerException(err);
        }
    }

    public static void requireNonNull(Object o, Object... objs) {
        requireNonNull(o);
        requireNonNull(objs);
    }

    public static void requireNonNull(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    public static void requireNonNull(Object[] objs) {
        for (Object obj : objs) {
            if (obj == null) {
                throw new NullPointerException();
            }
        }
    }

    public static <T> void requireNonEmpty(Collection<T> collection) {
        requireNonNull(collection);
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("unexpected empty collection!");
        }
    }

    public static void requireNotGreater(Number min, Number max) {
        requireNonNull(min);
        requireNonNull(max);
        if (Double.compare(min.doubleValue(), max.doubleValue()) > 0) {
            throw new IllegalArgumentException();
        }
    }

    public static void requireGreaterThanZero(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
    }
}

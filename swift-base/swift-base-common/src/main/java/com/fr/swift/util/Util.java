package com.fr.swift.util;

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

    public static void requireNonNull(Object o, Object... objs) {
        Assert.notNull(o);
        requireNonNull(objs);
    }

    public static void requireNonNull(Object o) {
        Assert.notNull(o);
    }

    public static void requireNonNull(Object[] objs) {
        for (Object obj : objs) {
            Assert.notNull(obj);
        }
    }

    public static <T> boolean isEmpty(T[] ts) {
        return ts == null || ts.length == 0;
    }

    public static boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    public static int hashCode(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    private static Throwable getRootCause(Throwable t) {
        Throwable cause = t.getCause();
        if (cause == null) {
            return t;
        }
        return getRootCause(cause);
    }

    public static String getRootCauseMessage(Throwable t) {
        Throwable rootCause = getRootCause(t);
        String message = rootCause.getMessage();
        String simpleThrowable = rootCause.getClass().getSimpleName();
        if (message == null) {
            return simpleThrowable;
        }
        return String.format("%s: %s", simpleThrowable, message);
    }

    public static <E> Optional<E> firstItemOf(Iterable<E> iterable) {
        if (iterable == null) {
            return Optional.empty();
        }
        for (E e : iterable) {
            return Optional.of(e);
        }
        return Optional.empty();
    }
}

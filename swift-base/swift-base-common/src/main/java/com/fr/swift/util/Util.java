package com.fr.swift.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    @Deprecated
    public static void requireNonNull(Object o, Object... objs) {
        Assert.notNull(o);
        requireNonNull(objs);
    }

    @Deprecated
    public static void requireNonNull(Object o) {
        Assert.notNull(o);
    }

    @Deprecated
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


    public static boolean isEqualCollection(final Collection a, final Collection b) {
        if (a != null) {
            if (b == null) {
                return false;
            }
            if (a.size() != b.size()) {
                return false;
            } else {
                Map mapa = getCardinalityMap(a);
                Map mapb = getCardinalityMap(b);
                if (mapa.size() != mapb.size()) {
                    return false;
                } else {
                    Iterator it = mapa.keySet().iterator();
                    while (it.hasNext()) {
                        Object obj = it.next();
                        if (getFreq(obj, mapa) != getFreq(obj, mapb)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return b == null;
    }

    public static Map getCardinalityMap(final Collection coll) {
        Map count = new HashMap();
        for (Iterator it = coll.iterator(); it.hasNext(); ) {
            Object obj = it.next();
            Integer c = (Integer) (count.get(obj));
            if (c == null) {
                count.put(obj, 1);
            } else {
                count.put(obj, c.intValue() + 1);
            }
        }
        return count;
    }

    private static final int getFreq(final Object obj, final Map freqMap) {
        Integer count = (Integer) freqMap.get(obj);
        if (count != null) {
            return count.intValue();
        }
        return 0;
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

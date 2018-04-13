package com.fr.swift.compare;

import com.fr.swift.util.Crasher;

import java.util.Comparator;

/**
 * 比较器工厂
 *
 * @author Daniel
 */
public class Comparators {

    /**
     * 专治中文String
     */
    public static final Comparator<String> PINYIN_ASC = new ChinesePinyinComparator();

    public static final Comparator<String> PINYIN_DESC = reverse(PINYIN_ASC);

    /**
     * 升序
     * <p>
     * examples:
     * 1.auto type-inference:
     * Comparator<String> c = Comparators.asc()
     * <p>
     * 2.infer the type explicitly:
     * Comparators.<Integer>asc().compare(1, 2)
     *
     * @param <T> type of object to be compared
     * @return result
     */
    public static <T extends Comparable<T>> Comparator<T> asc() {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                return o1.compareTo(o2);
            }
        };
    }

    public static <T extends Comparable<T>> Comparator<T> desc() {
        return reverse(Comparators.<T>asc());
    }

    public static <T> Comparator<T> reverse(final Comparator<T> c) {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return c.compare(o2, o1);
            }
        };
    }

    public static <T extends Number> Comparator<T> numberAsc() {
        return new Comparator<T>() {
            @Override
            public int compare(T a, T b) {
                if (a == b) {
                    return 0;
                }
                if (a == null) {
                    return -1;
                }
                if (b == null) {
                    return 1;
                }
                // 和数字运算类似，转成精度大的比较，默认最大为Double啦
                if (a instanceof Double || b instanceof Double) {
                    return Double.compare(a.doubleValue(), b.doubleValue());
                }
                if (a instanceof Long || b instanceof Long) {
                    return (((Long) a.longValue())).compareTo(b.longValue());
                }
                if (a instanceof Integer || b instanceof Integer) {
                    return ((Integer) a.intValue()).compareTo(b.intValue());
                }
                return Crasher.crash("cannot compare " + a.getClass() + " with " + b.getClass());
            }
        };
    }

    public static <T extends Number> Comparator<T> numberDesc() {
        return reverse(Comparators.<T>numberAsc());
    }
}
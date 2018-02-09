package com.fr.swift.compare;

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

    /**
     * 降序
     * <p>
     * examples:
     * 1.auto type-inference:
     * Comparator<String> c = Comparators.desc()
     * <p>
     * 2.infer the type explicitly:
     * Comparators.<Integer>desc().compare(1, 2)
     *
     * @param <T> type of object to be compared
     * @return result
     */
    public static <T extends Comparable<T>> Comparator<T> desc() {
        return new Comparator<T>() {
            private final Comparator<T> asc = asc();

            @Override
            public int compare(T o1, T o2) {
                return asc.compare(o2, o1);
            }
        };
    }

    public static <T> Comparator<T> reverse(final Comparator<T> c) {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return c.compare(o2, o1);
            }
        };
    }
}
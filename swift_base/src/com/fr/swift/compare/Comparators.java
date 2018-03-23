package com.fr.swift.compare;

import java.math.BigDecimal;
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
            public int compare(T a, T b){
                return new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString()));
            }
        };
    }

    public static <T extends Number> Comparator<T> numberDesc() {
        return reverse(Comparators.<T>numberAsc());
    }
}
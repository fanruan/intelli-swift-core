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
     * todo fr还是用pinyin，开源版本开放spi，由用户自定义排序方式
     */
    public static final Comparator<String> STRING_ASC = Comparators.asc();

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
                    return ((Long) a.longValue()).compareTo(b.longValue());
                }
                if (a instanceof Integer || b instanceof Integer) {
                    return ((Integer) a.intValue()).compareTo(b.intValue());
                }
                if (a instanceof Short || b instanceof Short) {
                    return ((Short) a.shortValue()).compareTo(b.shortValue());
                }
                if (a instanceof Byte || b instanceof Byte) {
                    return ((Byte) a.byteValue()).compareTo(b.byteValue());
                }
                return Crasher.crash("cannot compare " + a.getClass() + " with " + b.getClass());
            }
        };
    }

    private Comparators() {
        throw new InstantiationError();
    }

    public static final Comparable<?> MIN_INFINITY = new Comparable<Object>() {
        @Override
        public int compareTo(Object o) {
            return -1;
        }
    };

    public static final Comparable<?> MAX_INFINITY = new Comparable<Object>() {
        @Override
        public int compareTo(Object o) {
            return 1;
        }
    };

    public static final String MIN_INFINITY_STRING = new String("I am MIN_INFINITY_STRING");

    public static final String MAX_INFINITY_STRING = new String("I am MAX_INFINITY_STRING");
}
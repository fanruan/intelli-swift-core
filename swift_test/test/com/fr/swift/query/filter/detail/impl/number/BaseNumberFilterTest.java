package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.BaseFilterTest;
import com.fr.swift.query.filter.detail.impl.ColumnImplTest;
import com.fr.swift.segment.column.Column;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/12/1.
 */
public abstract class BaseNumberFilterTest extends BaseFilterTest {

    protected static Random random = new Random(23854);
    protected static List<Integer> intDetails;
    protected static List<Double> doubleDetails;
    protected static List<Long> longDetails;

    protected static Column intColumn;
    protected static Column doubleColumn;
    protected static Column longColumn;
    protected List<Integer> expectedIndexes;

    static {
        initDetails();
        initColumn();
    }

    protected List<? extends Number> details;
    protected boolean isNot = false;
    protected Column column;
    protected DetailFilter filter;

    protected abstract List<Integer> getExpectedIndexes();

    public static class NumberComparator<T extends Number> implements Comparator<Number> {
        @Override
        public int compare(Number o1, Number o2) {
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            Double d1 = o1.doubleValue();
            Double d2 = o2.doubleValue();
            return d1.compareTo(d2);
        }
    }

    private static void initColumn() {
        intColumn = new ColumnImplTest<Integer>(intDetails, new NumberComparator<Integer>(), null) {
            @Override
            protected Integer convertValue(Object value) {
                return ((Number)value).intValue();
            }
        };
        doubleColumn = new ColumnImplTest<Double>(doubleDetails, new NumberComparator<Double>(), null) {
            @Override
            protected Double convertValue(Object value) {
                return ((Number) value).doubleValue();
            }
        };
        longColumn = new ColumnImplTest<Long>(longDetails, new NumberComparator<Long>(), null) {
            @Override
            protected Long convertValue(Object value) {
                return ((Number) value).longValue();
            }
        };
    }

    private static void initDetails() {
        List<Integer> integers = IntStream.range(0, 100).map(i -> random.nextInt(10000))
                .mapToObj(Integer::new).collect(Collectors.toList());
        intDetails = IntStream.range(0, integers.size() * 5)
                .mapToObj(i -> integers.get(random.nextInt(integers.size()))).collect(Collectors.toList());

        List<Long> longs = IntStream.range(0, 100).map(i -> random.nextInt(10000))
                .mapToObj(Long::new).collect(Collectors.toList());
        longDetails = IntStream.range(0, longs.size() * 5)
                .mapToLong(i -> longs.get(random.nextInt(longs.size()))).mapToObj(Long::new).collect(Collectors.toList());

        List<Double> doubles = IntStream.range(0, 100).mapToDouble(i -> i + random.nextDouble())
                .mapToObj(Double::new).collect(Collectors.toList());
        doubleDetails = IntStream.range(0, longs.size() * 5)
                .mapToDouble(i -> doubles.get(random.nextInt(doubles.size())))
                .mapToObj(Double::new).collect(Collectors.toList());
    }

    protected Number getRandomMatchedNumber() {
        if (expectedIndexes.size() == 0) {
            return null;
        }
        while (true) {
            int i = random.nextInt(details.size());
            if (expectedIndexes.contains(i)) {
                return details.get(i);
            }
        }
    }

    protected Number getRandomNotMatchedNumber() {
        if (expectedIndexes.size() == details.size()) {
            return null;
        }
        while (true) {
            int i = random.nextInt(details.size());
            if (!expectedIndexes.contains(i)) {
                return details.get(i);
            }
        }
    }
}

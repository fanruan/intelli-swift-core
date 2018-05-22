package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.ToStringConverter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/12/1.
 */
public class NumberInRangeFilterTest extends BaseNumberFilterTest {

    static Class[] doubleType = new Class[]{int.class, Double.class, Double.class, boolean.class, boolean.class, Column.class};

    private Number min;
    private Number max;
    private boolean minIncluded = true;
    private boolean maxIncluded = true;
    protected Class<? extends DetailFilter> filterClass;
    private List<Class> parameters = new ArrayList<>(Arrays.asList(doubleType));
    private List<Range> ranges = new ArrayList<>();

    @Override
    public void setUp() throws Exception {
        this.filterClass = NumberInRangeFilter.class;
    }

    private void prepare(List<? extends Number> details,
                         Class<? extends DetailFilter> filterClass, Column column) {
        this.details = details;
        this.filterClass = filterClass;
        this.column = column;
    }

    public void testIntColumn() {
        prepare(intDetails, filterClass, intColumn);
        start();
    }

    public void testDoubleColumn() {
        prepare(doubleDetails, filterClass, doubleColumn);
        start();
    }

    public void testLongColumn() {
        prepare(longDetails, filterClass, longColumn);
        start();
    }

    private void start() {
        minGroupExisting();
        minGroupNotExisting();
        minOutOfRange();
        maxOutOfRange();
    }

    private void minGroupExisting() {
        ranges.clear();
        min = getRandomGroup();
        max = getGreaterGroup(min);
        addRange(min, max);
        max = getGreaterNotExistGroup(min);
        addRange(min, max);
        performTest();
    }

    private void minGroupNotExisting() {
        ranges.clear();
        min = getRandomNotExistGroup();
        max = getGreaterGroup(min);
        addRange(min, max);
        max = getGreaterNotExistGroup(min);
        addRange(min, max);
        performTest();
    }

    private void minOutOfRange() {
        ranges.clear();
        int leftOffset = -20000;
        int rightOffset = 10000;
        min = getOutOfRangeGroup(leftOffset);
        max = getRandomGroup();
        addRange(min, max);
        min = getOutOfRangeGroup(rightOffset);
        addRange(min, min); // min > 上界
        performTest();
    }

    private void maxOutOfRange() {
        ranges.clear();
        int leftOffset = -20000;
        int rightOffset = 10000;
        min = getRandomGroup();
        max = getOutOfRangeGroup(leftOffset);
        addRange(max, max); // max < 下界
        max = getOutOfRangeGroup(rightOffset);
        addRange(min, max);
        performTest();
    }

    private void performTest() {
        ranges.stream().forEach(range -> {
            min = range.min;
            max = range.max;
            minIncluded = range.minIncluded;
            maxIncluded = range.maxIncluded;
            updateFilter();
            turnOnFilter();
            turnOnMatcher();
        });
    }

    private void addRange(Number min, Number max) {
        ranges.add(new Range(min, max, true, true));
        ranges.add(new Range(min, max, true, false));
        ranges.add(new Range(min, max, false, true));
        ranges.add(new Range(min, max, false, false));
    }

    private void turnOnFilter() {
        ImmutableBitMap bitMap = filter.createFilterIndex();
        assertEquals(bitMap.getCardinality(), expectedIndexes.size());
        IntStream.range(0, expectedIndexes.size()).forEach(i -> assertTrue(bitMap.contains(expectedIndexes.get(i))));
    }

    private void turnOnMatcher() {
        SwiftNode node = createNode(getRandomMatchedNumber());
        SwiftNode node1 = createNode(getRandomNotMatchedNumber());
        if (node.getData() == null) {
            assertTrue(!filter.matches(node, 0, new ToStringConverter()));
        } else {
            assertTrue(filter.matches(node, 0, new ToStringConverter()));
        }
        assertTrue(!filter.matches(node1, 0, new ToStringConverter()));
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
        if (isNot) {
            return IntStream.range(0, details.size()).filter(i -> !isInRange(details.get(i).doubleValue()))
                    .mapToObj(Integer::new).collect(Collectors.toList());
        }
        return IntStream.range(0, details.size()).filter(i -> isInRange(details.get(i).doubleValue()))
                .mapToObj(Integer::new).collect(Collectors.toList());
    }

    private boolean isInRange(double value) {
        double minValue = min.doubleValue();
        double maxValue = max.doubleValue();
        return (minIncluded ? value >= minValue : value > minValue) &&
                (maxIncluded ? value <= maxValue : value < maxValue);
    }

    private void updateFilter() {
        try {
            Constructor<? extends DetailFilter> c;
            if (isNot) {
                c = filterClass.getDeclaredConstructor(parameters.toArray(new Class[6]));
                this.filter = c.newInstance(details.size(), min, max, minIncluded, maxIncluded, column);
            } else {
                c = filterClass.getDeclaredConstructor(parameters.subList(1, parameters.size()).toArray(new Class[5]));
                this.filter = c.newInstance(min, max, minIncluded, maxIncluded, column);
            }
            expectedIndexes = getExpectedIndexes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Number getRandomGroup() {
        return details.get(random.nextInt(details.size()));
    }

    private Number getOutOfRangeGroup(int offset) {
        while (true) {
            Number n = getRandomGroup();
            if (n instanceof Double) {
                n = n.doubleValue() + offset;
            } else if (n instanceof Integer) {
                n = n.intValue() + offset;
            } else {
                n = n.longValue() + offset;
            }
            if (n.doubleValue() < 0 || n.doubleValue() > 10000) {
                return n;
            }
        }
    }

    private Number getRandomNotExistGroup() {
        while (true) {
            Number n = getRandomGroup();
            if (n instanceof Double) {
                n = n.doubleValue() + 1;
            } else if (n instanceof Integer) {
                n = n.intValue() + 1;
            } else {
                n = n.longValue() + 1;
            }
            if (details.indexOf(n) == -1) {
                return n;
            }
        }
    }

    private Number getGreaterGroup(Number min) {
        while (true) {
            Number n = details.get(random.nextInt(details.size()));
            if (n.doubleValue() > min.doubleValue()) {
                return n;
            }
        }
    }

    private Number getGreaterNotExistGroup(Number number) {
        while (true) {
            Number n = getRandomNotExistGroup();
            if (n.doubleValue() > number.doubleValue()) {
                return n;
            }
        }
    }

    private static class Range {
        double min;
        double max;
        boolean minIncluded;
        boolean maxIncluded;

        public Range(Number min, Number max, boolean minIncluded, boolean maxIncluded) {
            this.min = min.doubleValue();
            this.max = max.doubleValue();
            this.minIncluded = minIncluded;
            this.maxIncluded = maxIncluded;
        }
    }
}

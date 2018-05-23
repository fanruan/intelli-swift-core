package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.ToStringConverter;
import com.fr.swift.segment.column.Column;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/12/1.
 */
public class NumberContainFilterTest extends BaseNumberFilterTest {

    protected Set groups = new HashSet<>();

    protected DetailFilter createFilter(Column column) {
        return new NumberContainFilter(groups, column);
    }

    private void prepare(List details, Column column) {
        this.details = details;
        IntStream.range(0, 10).forEach(i -> groups.add(new Double(details.get(random.nextInt(details.size())).toString())));
        filter = createFilter(column);
        expectedIndexes = getExpectedIndexes();
    }

    public void testInt() {
        prepare(intDetails, intColumn);
        filter();
        match();
    }

    public void testDouble() {
        prepare(doubleDetails, doubleColumn);
        filter();
        match();
    }

    public void testLong() {
        prepare(longDetails, longColumn);
        filter();
        match();
    }

    public void filter() {
        ImmutableBitMap bitMap = filter.createFilterIndex();
        assertEquals(bitMap.getCardinality(), expectedIndexes.size());
        IntStream.range(0, expectedIndexes.size()).forEach(i -> assertTrue(bitMap.contains(expectedIndexes.get(i))));
    }

    public void match() {
        assertTrue(!filter.matches(createNode(null), 0, new ToStringConverter()));
        assertTrue(filter.matches(createNode(getRandomMatchedNumber()), 0, new ToStringConverter()));
        assertTrue(!filter.matches(createNode(getRandomNotMatchedNumber()), 0, new ToStringConverter()));
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
        if (isNot) {
            return IntStream.range(0, details.size()).filter(i -> !groups.contains(((Number) details.get(i)).doubleValue()))
                    .mapToObj(Integer::new).collect(Collectors.toList());
        }
        return IntStream.range(0, details.size()).filter(i -> groups.contains(((Number) details.get(i)).doubleValue()))
                .mapToObj(Integer::new).collect(Collectors.toList());
    }
}

package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.InFilter;
import com.fr.swift.query.filter.match.ToStringConverter;
import com.fr.swift.segment.column.Column;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2017/12/1.
 */
public class NumberContainFilterTest extends BaseNumberFilterTest {

    protected Set groups = new HashSet<Object>();

    protected DetailFilter createFilter(Column column) {
        return new InFilter(groups, column);
    }

    private void prepare(List details, Column column) {
        this.details = details;
//        IntStream.range(0, 10).forEach(i -> groups.add(details.get(random.nextInt(details.size()))));
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
//        IntStream.range(0, expectedIndexes.size()).forEach(i -> assertTrue(bitMap.contains(expectedIndexes.get(i))));
    }

    public void match() {
        assertTrue(!filter.matches(createNode(null), 0, new ToStringConverter()));
        assertTrue(filter.matches(createNode(getRandomMatchedNumber()), 0, null));
        assertTrue(!filter.matches(createNode(getRandomNotMatchedNumber()), 0, null));
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
//        if (isNot) {
//            return IntStream.range(0, details.size()).filter(i -> !groups.contains(details.get(i)))
//                    .mapToObj(Integer::new).collect(Collectors.toList());
//        }
//        return IntStream.range(0, details.size()).filter(i -> groups.contains(details.get(i)))
//                .mapToObj(Integer::new).collect(Collectors.toList());
        return null;
    }
}

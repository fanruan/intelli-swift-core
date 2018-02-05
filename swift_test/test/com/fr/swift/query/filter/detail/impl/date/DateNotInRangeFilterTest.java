package com.fr.swift.query.filter.detail.impl.date;

import com.fr.swift.query.filter.detail.impl.date.not.DateNotInRangeFilter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/12/5.
 */
public class DateNotInRangeFilterTest extends DateInRangeFilterTest {

    public DateNotInRangeFilterTest() {
        this.filter = new DateNotInRangeFilter(details.size(), startDate, endDate, column);
    }

    @Override
    public void testMatch() {
        assertTrue(!filter.matches(createNode(startDate, comparator)));
        assertTrue(!filter.matches(createNode(endDate, comparator)));
        assertTrue(filter.matches(createNode(getRandomMatchedDetail(details, expectedIndexes), comparator)));
        assertTrue(!filter.matches(createNode(getRandomNotMatchedDetail(details, expectedIndexes), comparator)));
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
        return IntStream.range(0, details.size()).filter(i -> {
            Long date = details.get(i);
            return comparator.compare(date, startDate) < 0 || comparator.compare(date, endDate) > 0;
        }).mapToObj(Integer::new).collect(Collectors.toList());
    }
}

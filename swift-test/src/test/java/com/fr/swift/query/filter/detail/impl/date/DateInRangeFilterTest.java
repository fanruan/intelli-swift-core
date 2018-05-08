package com.fr.swift.query.filter.detail.impl.date;

import com.fr.swift.query.filter.detail.impl.BaseColumnImplTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/12/5.
 */
public class DateInRangeFilterTest extends BaseDateFilterTest {

    private List<Long> groups = ((BaseColumnImplTest) column).getGroups();
    protected Long startDate;
    protected Long endDate;

    public DateInRangeFilterTest() {
        init();
        this.filter = new DateInRangeFilter(startDate, endDate, column);
    }

    @Override
    public void testMatch() {
        assertTrue(filter.matches(createNode(startDate, comparator), 0));
        assertTrue(filter.matches(createNode(endDate, comparator), 0));
        assertTrue(filter.matches(createNode(getRandomMatchedDetail(details, expectedIndexes), comparator), 0));
        assertTrue(!filter.matches(createNode(getRandomNotMatchedDetail(details, expectedIndexes), comparator), 0));
    }

    private void init() {
        Long start = groups.get(random.nextInt(groups.size()));
        Long end = groups.get(random.nextInt(groups.size()));
        if (comparator.compare(start, end) <= 0) {
            this.startDate = start;
            this.endDate = end;
        } else {
            this.startDate = end;
            this.endDate = start;
        }
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
        return IntStream.range(0, details.size()).filter(i -> {
            Long date = details.get(i);
            return comparator.compare(date, startDate) >= 0 && comparator.compare(date, endDate) <= 0;
        }).mapToObj(Integer::new).collect(Collectors.toList());
    }
}

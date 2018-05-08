package com.fr.swift.query.filter.detail.impl.nfilter;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.impl.BaseColumnImplTest;
import com.fr.swift.query.filter.detail.impl.number.BaseNumberFilterTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/12/5.
 */
public class TopNFilterTest extends BaseNumberFilterTest {

    private int topN;
    private List<Double> groups;

    public TopNFilterTest() {
        this.column = doubleColumn;
        this.details = doubleDetails;
        this.groups = new ArrayList<>(((BaseColumnImplTest) column).getGroups());
        topN = groups.size() / 2;
        this.filter = new TopNFilter(topN, column);
    }

    public void testFilter() {
        expectedIndexes = getExpectedIndexes();
        ImmutableBitMap bitMap = filter.createFilterIndex();
        assertEquals(bitMap.getCardinality(), expectedIndexes.size());
        IntStream.range(0, expectedIndexes.size()).forEach(i -> assertTrue(bitMap.contains(expectedIndexes.get(i))));
    }

    public void testMatch() {
        assertTrue(filter.matches(createNode(random.nextInt(topN), 0), 0));
        assertTrue(!filter.matches(createNode(topN, 0), 0));
        assertTrue(filter.matches(createNode(topN - 1, 0), 0));
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
        Set<Double> topNGroups = new HashSet<>(groups.subList(topN + 1, groups.size()));
        return IntStream.range(0, details.size()).filter(i -> topNGroups.contains(details.get(i)))
                .mapToObj(Integer::new).collect(Collectors.toList());
    }
}

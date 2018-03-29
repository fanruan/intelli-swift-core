package com.fr.swift.query.filter.detail.impl.nfilter;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.impl.ColumnImplTest;
import com.fr.swift.query.filter.detail.impl.number.BaseNumberFilterTest;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/12/5.
 */
public class BottomNFilterTest extends BaseNumberFilterTest {

    private int bottomN;
    private List<Long> groups;

    public BottomNFilterTest() {
        this.column = longColumn;
        this.details = longDetails;
        this.groups = new ArrayList<>(((ColumnImplTest) column).getGroups());
        this.bottomN = groups.size() / 2;
        this.filter = new BottomNFilter(bottomN, column);
    }

    public void testFilter() {
        expectedIndexes = getExpectedIndexes();
        ImmutableBitMap bitMap = filter.createFilterIndex();
        assertEquals(bitMap.getCardinality(), expectedIndexes.size());
        IntStream.range(0, expectedIndexes.size()).forEach(i -> assertTrue(bitMap.contains(expectedIndexes.get(i))));
    }

    public void testMatch() {
        assertTrue(filter.matches(createNode(groups.size() - random.nextInt(bottomN), groups.size())));
        assertTrue(!filter.matches(createNode(groups.size() - bottomN, groups.size())));
        assertTrue(filter.matches(createNode(groups.size() - bottomN + 1, groups.size())));
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
        Set<Long> bottomNGroups = new HashSet<>(groups.subList(DictionaryEncodedColumn.NOT_NULL_START_INDEX, bottomN + 1));
        return IntStream.range(0, details.size()).filter(i -> bottomNGroups.contains(details.get(i)))
                .mapToObj(Integer::new).collect(Collectors.toList());
    }
}

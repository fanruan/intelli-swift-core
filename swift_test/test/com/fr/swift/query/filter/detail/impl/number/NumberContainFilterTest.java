package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.bitmap.ImmutableBitMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/12/1.
 */
public abstract class NumberContainFilterTest extends BaseNumberFilterTest {

    protected Set groups = new HashSet<>();

    public NumberContainFilterTest(List<? extends Number> details) {
        super();
        this.details = details;
        init();
    }

    @Override
    protected void setUp() throws Exception {
        expectedIndexes = getExpectedIndexes();
    }

    public void testFilter() {
        ImmutableBitMap bitMap = filter.createFilterIndex();
        assertEquals(bitMap.getCardinality(), expectedIndexes.size());
        IntStream.range(0, expectedIndexes.size()).forEach(i -> assertTrue(bitMap.contains(expectedIndexes.get(i))));
    }

    public void testMatch() {
        assertTrue(!filter.matches(createNode(null)));
        assertTrue(filter.matches(createNode(getRandomMatchedNumber())));
        assertTrue(!filter.matches(createNode(getRandomNotMatchedNumber())));
    }

    private void init() {
        IntStream.range(0, 10)
                .forEach(i -> groups.add(details.get(random.nextInt(details.size()))));
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
        if (isNot) {
            return IntStream.range(0, details.size()).filter(i -> !groups.contains(details.get(i)))
                    .mapToObj(Integer::new).collect(Collectors.toList());
        }
        return IntStream.range(0, details.size()).filter(i -> groups.contains(details.get(i)))
                .mapToObj(Integer::new).collect(Collectors.toList());
    }
}

package com.fr.swift.query.filter.detail.impl.string;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.compare.Comparators;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.BaseColumnImplTest;
import com.fr.swift.query.filter.detail.impl.BaseFilterTest;
import com.fr.swift.query.filter.match.ToStringConverter;
import com.fr.swift.segment.column.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lyon on 2017/11/30.
 */
public abstract class BaseStringFilterTest extends BaseFilterTest {

    protected static List<String> words = prepareWords(100);
    //    protected static Column wordsColumn = new BaseColumnImplTest(words, Comparator.naturalOrder(), NULL_VALUE);
    protected static Column wordsColumn = new BaseColumnImplTest(words, Comparators.asc(), NULL_VALUE) {
        @Override
        protected Object convertValue(Object value) {
            return value;
        }
    };

    protected List<String> details;
    protected Column column;
    protected DetailFilter filter;
    protected List<Integer> expectedIndexes;

    public BaseStringFilterTest() {
        this.details = words;
        this.column = wordsColumn;
    }

    protected abstract List<Integer> getExpectedIndexes();

    @Override
    protected void setUp() throws Exception {
        expectedIndexes = getExpectedIndexes();
    }

    public void testFilter() {
        ImmutableBitMap bitMap = filter.createFilterIndex();
        assertEquals(bitMap.getCardinality(), expectedIndexes.size());
//        IntStream.range(0, expectedIndexes.size()).forEach(i -> assertTrue(bitMap.contains(expectedIndexes.get(i))));
    }

    public void testMatch() {
        assertTrue(filter.matches(createNode(getRandomMatchedDetail(details, expectedIndexes)), 0, new ToStringConverter()));
        assertTrue(!filter.matches(createNode(getRandomNotMatchedDetail(details, expectedIndexes)), 0, new ToStringConverter()));
        assertTrue(!filter.matches(createNode(null), 0, new ToStringConverter()));
    }

    private static String getWord() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        List<String> letters = new ArrayList<String>(Arrays.asList(alphabet.split("")));
        int len = random.nextInt(7) + 1;
//        return IntStream.range(0, len)
//                .mapToObj(i -> letters.get(random.nextInt(26))).collect(Collectors.joining());
        return null;
    }

    private static List<String> prepareWords(int count) {
//        List<String> groups = IntStream.range(0, count).mapToObj(i -> getWord()).collect(Collectors.toList());
//        // 添加空值
//        IntStream.range(0, random.nextInt(5)).forEach(i -> groups.add(random.nextInt(groups.size()), NULL_VALUE));
//        List<String> details = IntStream.range(0, groups.size() * 5)
//                .mapToObj(i -> groups.get(random.nextInt(groups.size()))).collect(Collectors.toList());
        return null;
    }
}


package com.fr.swift.query.filter.detail.impl.string;

import com.fr.swift.bitmap.ImmutableBitMap;

import java.util.List;

/**
 * Created by Lyon on 2017/11/30.
 */
public class StringEndsWithFilterTest extends BaseStringFilterTest {

    protected String endsWith;

    public StringEndsWithFilterTest() {
        init();
        this.filter = new StringEndsWithFilter(endsWith, column);
    }

    private void init() {
        String word = getRandomDetail(details);
        endsWith = word.split("")[word.length() - 1];
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
//        return IntStream.range(0, details.size()).filter(i -> details.get(i).endsWith(endsWith))
//                .mapToObj(Integer::new).collect(Collectors.toList());
        return null;
    }

    public void testNotExistGroup() {
        String endsWith = "fdgsahjgfkdsfgd";
        StringEndsWithFilter filter = new StringEndsWithFilter(endsWith, column);
        ImmutableBitMap bitMap = filter.createFilterIndex();
        assertTrue(bitMap.isEmpty());
    }
}

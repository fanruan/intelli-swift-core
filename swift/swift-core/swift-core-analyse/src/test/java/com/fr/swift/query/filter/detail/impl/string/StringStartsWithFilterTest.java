package com.fr.swift.query.filter.detail.impl.string;

import com.fr.swift.bitmap.ImmutableBitMap;

import java.util.List;

/**
 * Created by Lyon on 2017/11/30.
 */
public class StringStartsWithFilterTest extends BaseStringFilterTest {

    protected String startsWith;

    public StringStartsWithFilterTest() {
        init();
        this.filter = new StringStartsWithFilter(startsWith, column);
    }

    private void init() {
        String word = getRandomDetail(details);
        startsWith = word.split("")[0];
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
//        return IntStream.range(0, details.size()).filter(i -> details.get(i).startsWith(startsWith))
//                .mapToObj(Integer::new).collect(Collectors.toList());
        return null;
    }

    public void testNotExistGroup() {
        String startsWith = "dsafdsagdsafd";
        StringStartsWithFilter filter = new StringStartsWithFilter(startsWith, column);
        ImmutableBitMap bitMap = filter.createFilterIndex();
        assertTrue(bitMap.isEmpty());
    }
}

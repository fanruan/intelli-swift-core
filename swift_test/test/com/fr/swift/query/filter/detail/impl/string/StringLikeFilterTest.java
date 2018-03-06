package com.fr.swift.query.filter.detail.impl.string;

import com.fr.swift.bitmap.ImmutableBitMap;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/11/30.
 */
public class StringLikeFilterTest extends BaseStringFilterTest {

    protected String like;

    public StringLikeFilterTest() {
        init();
        this.filter = new StringLikeFilter(like, column);
    }

    private void init() {
        String word = getRandomDetail(details);
        like = word.split("")[random.nextInt(word.length())];
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
        return IntStream.range(0, details.size()).filter(i -> details.get(i).contains(like))
                .mapToObj(Integer::new).collect(Collectors.toList());
    }

    public void testNotExistGroup() {
        String like = "afdagdghdsgdfss";
        StringLikeFilter filter = new StringLikeFilter(like, column);
        ImmutableBitMap bitMap = filter.createFilterIndex();
        assertTrue(bitMap.isEmpty());
    }
}

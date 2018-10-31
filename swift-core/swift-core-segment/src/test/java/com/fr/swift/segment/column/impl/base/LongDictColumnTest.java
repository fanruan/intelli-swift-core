package com.fr.swift.segment.column.impl.base;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import org.junit.Before;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author anchore
 * @date 2017/11/11
 */
public class LongDictColumnTest extends BaseDictColumnTest<Long> {

    @Before
    public void setUp() throws Exception {
        c = Comparators.asc();
        Set<Long> longs = new TreeSet<Long>(c);
        while (longs.size() < size) {
            longs.add(r.nextLong());
        }
        values = longs.toArray(new Long[]{});
    }

    @Override
    DictionaryEncodedColumn<Long> getDictColumn() {
        return new LongDictColumn(
                new ResourceLocation(BASE_PATH + "/dict/child_long"),
                c);
    }

}
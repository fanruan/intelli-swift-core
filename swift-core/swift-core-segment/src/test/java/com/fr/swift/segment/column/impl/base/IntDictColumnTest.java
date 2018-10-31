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
public class IntDictColumnTest extends BaseDictColumnTest<Integer> {

    @Before
    public void setUp() throws Exception {
        c = Comparators.asc();
        Set<Integer> ints = new TreeSet<Integer>(c);
        while (ints.size() < size) {
            ints.add(r.nextInt(size << 1));
        }
        values = ints.toArray(new Integer[]{});
    }

    @Override
    DictionaryEncodedColumn<Integer> getDictColumn() {
        return new IntDictColumn(
                new ResourceLocation(BASE_PATH + "/dict/child_int"),
                c);
    }

}

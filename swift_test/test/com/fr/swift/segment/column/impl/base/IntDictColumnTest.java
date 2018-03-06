package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.stream.Stream;

/**
 * @author anchore
 * @date 2017/11/11
 */
public class IntDictColumnTest extends BaseDictColumnTest<Integer> {

    int size = 10000;

    {
        c = Integer::compareTo;
        values = Stream.generate(() -> r.nextInt(size << 1)).limit(size).distinct().sorted(c).toArray(Integer[]::new);
    }

    @Override
    DictionaryEncodedColumn<Integer> getDictColumn() {
        return new IntDictColumn(
                new ResourceLocation(BASE_PATH + "/dict/child_int"),
                c);
    }

}

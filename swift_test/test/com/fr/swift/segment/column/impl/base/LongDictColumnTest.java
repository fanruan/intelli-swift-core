package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.stream.Stream;

/**
 * @author anchore
 * @date 2017/11/11
 */
public class LongDictColumnTest extends BaseDictColumnTest<Long> {
    int size = 10000;

    {
        c = Long::compareTo;
        values = Stream.generate(r::nextLong).limit(size).sorted(c).distinct().toArray(Long[]::new);
    }

    @Override
    DictionaryEncodedColumn<Long> getDictColumn() {
        return new LongDictColumn(
                new ResourceLocation(BASE_PATH + "/dict/child_long"),
                c);
    }

}
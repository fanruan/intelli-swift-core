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
public class DoubleDictColumnTest extends BaseDictColumnTest<Double> {

    @Before
    public void setUp() throws Exception {
        c = Comparators.asc();
        Set<Double> doubles = new TreeSet<Double>(c);
        while (doubles.size() < size) {
            doubles.add(r.nextDouble());
        }
        values = doubles.toArray(new Double[]{});
    }

    @Override
    DictionaryEncodedColumn<Double> getDictColumn() {
        return new DoubleDictColumn(
                new ResourceLocation(BASE_PATH + "/dict/child_double"),
                c);
    }

}
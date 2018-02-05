package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.location.ResourceLocation;

/**
 * @author anchore
 * @date 2017/11/10
 */
public class DoubleDetailColumnTest extends BasePrimitiveDetailColumnTest {

    @Override
    public void testPutThenGet() {
        double[] doubles = r.doubles(size, 0, size << 1).sorted().toArray();
        DoubleDetailColumn doubleDetailColumn = new DoubleDetailColumn(
                new ResourceLocation(BASE_PATH + "/detail/child_double"));
        for (int i = 0; i < doubles.length; i++) {
            doubleDetailColumn.put(i, doubles[i]);
        }
        doubleDetailColumn.release();

        doubleDetailColumn = new DoubleDetailColumn(
                new ResourceLocation(BASE_PATH + "/detail/child_double"));
        for (int i = 0; i < doubles.length; i++) {
            assertEquals(doubles[i], doubleDetailColumn.getDouble(i));
        }
    }

}

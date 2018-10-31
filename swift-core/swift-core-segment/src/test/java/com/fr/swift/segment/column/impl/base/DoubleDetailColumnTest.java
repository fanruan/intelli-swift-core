package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.location.ResourceLocation;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2017/11/10
 */
public class DoubleDetailColumnTest extends BaseDetailColumnTest {

    @Test
    public void testPutThenGet() {
        double[] doubles = new double[size];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = r.nextDouble();
        }
        Arrays.sort(doubles);
        DoubleDetailColumn doubleDetailColumn = new DoubleDetailColumn(
                new ResourceLocation(BASE_PATH + "/detail/child_double"));
        for (int i = 0; i < doubles.length; i++) {
            doubleDetailColumn.put(i, doubles[i]);
        }
        doubleDetailColumn.release();

        doubleDetailColumn = new DoubleDetailColumn(
                new ResourceLocation(BASE_PATH + "/detail/child_double"));
        for (int i = 0; i < doubles.length; i++) {
            assertEquals(doubles[i], doubleDetailColumn.getDouble(i), 0);
        }
    }

}

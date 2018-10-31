package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.location.ResourceLocation;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2017/11/10
 */
public class IntDetailColumnTest extends BaseDetailColumnTest {

    @Test
    public void testPutThenGet() {
        int[] ints = new int[size];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = r.nextInt(ints.length << 1);
        }
        Arrays.sort(ints);
        IntDetailColumn intDetailColumn = new IntDetailColumn(
                new ResourceLocation(BASE_PATH + "/detail/child_int"));
        for (int i = 0; i < ints.length; i++) {
            intDetailColumn.put(i, ints[i]);
        }
        intDetailColumn.release();

        intDetailColumn = new IntDetailColumn(
                new ResourceLocation(BASE_PATH + "/detail/child_int"));
        for (int i = 0; i < ints.length; i++) {
            assertEquals(ints[i], intDetailColumn.getInt(i));
        }
    }

}

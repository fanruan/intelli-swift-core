package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.location.ResourceLocation;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2018/10/30
 */
public class DateDetailColumnTest extends BaseDetailColumnTest {

    @Test
    public void testPutThenGet() {
        Date[] dates = new Date[size];
        for (int i = 0; i < dates.length; i++) {
            dates[i] = new Date(r.nextLong());
        }
        Arrays.sort(dates);
        DateDetailColumn dateDetailColumn = new DateDetailColumn(
                new ResourceLocation(BASE_PATH + "/detail/child_date"));
        for (int i = 0; i < dates.length; i++) {
            dateDetailColumn.put(i, dates[i]);
        }
        dateDetailColumn.release();

        dateDetailColumn = new DateDetailColumn(
                new ResourceLocation(BASE_PATH + "/detail/child_date"));
        for (int i = 0; i < dates.length; i++) {
            assertEquals(dates[i], dateDetailColumn.get(i));
        }
    }
}